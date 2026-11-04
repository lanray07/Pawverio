package com.pawverio.platform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale
import java.util.concurrent.TimeUnit

object QuietHours {
    fun contains(hour: Int, start: Int, end: Int): Boolean =
        when {
            start == end -> false
            start < end -> hour in start until end
            else -> hour >= start || hour < end
        }
}

class ReminderPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("reminder-preferences", Context.MODE_PRIVATE)
    var localeTag: String
        get() = prefs.getString("localeTag", null)?.takeIf { it.isNotBlank() } ?: Locale.getDefault().toLanguageTag()
        set(value) {
            prefs.edit().putString("localeTag", value).apply()
        }

    var enabled: Boolean
        get() = prefs.getBoolean("enabled", true)
        set(value) {
            prefs.edit().putBoolean("enabled", value).apply()
        }

    var quietStart: Int
        get() = prefs.getInt("quietStart", 22)
        set(value) {
            require(value in 0..23)
            prefs.edit().putInt("quietStart", value).apply()
        }

    var quietEnd: Int
        get() = prefs.getInt("quietEnd", 7)
        set(value) {
            require(value in 0..23)
            prefs.edit().putInt("quietEnd", value).apply()
        }

    var quietEnabled: Boolean
        get() = prefs.getBoolean("quiet", false)
        set(value) {
            prefs.edit().putBoolean("quiet", value).apply()
        }
}

/** WorkManager is intentionally inexact. Fire OS power restrictions can delay delivery. */
class ReminderScheduler(private val context: Context) {
    fun schedule(
        id: String,
        title: String,
        body: String,
        dueAtMillis: Long,
        intervalDays: Int = 0,
        endAtMillis: Long? = null,
        recurrenceAnchorMillis: Long = dueAtMillis,
    ) {
        require(intervalDays >= 0)
        if (endAtMillis != null && dueAtMillis > endAtMillis) {
            cancel(id)
            return
        }
        val fingerprintSource =
            listOf(
                    title,
                    body,
                    dueAtMillis.toString(),
                    intervalDays.toString(),
                    endAtMillis.toString(),
                    recurrenceAnchorMillis.toString(),
                    ZoneId.systemDefault().id,
                )
                .joinToString("") { "${it.length}:$it" }
        val fingerprint =
            java.security.MessageDigest.getInstance("SHA-256")
                .digest(fingerprintSource.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
        val token = java.util.UUID.randomUUID().toString()
        val data =
            Data.Builder()
                .putString("id", id)
                .putString("title", title)
                .putString("body", body)
                .putLong("due", dueAtMillis)
                .putLong("anchor", recurrenceAnchorMillis)
                .putLong("end", endAtMillis ?: Long.MAX_VALUE)
                .putInt("days", intervalDays)
                .putString("generation", token)
                .build()
        synchronized(lock) {
            val prefs = generations(context)
            if (
                prefs.getString("$id.fingerprint", null) == fingerprint &&
                    prefs.getString(id, null) != null
            )
                return
            prefs.edit().putString(id, token).putString("$id.fingerprint", fingerprint).commit()
            context.getSystemService(NotificationManager::class.java).cancel(id.hashCode())
            enqueue(context, id, data, dueAtMillis)
        }
    }

    fun cancel(id: String) {
        synchronized(lock) {
            generations(context).edit().remove(id).remove("$id.fingerprint").commit()
            WorkManager.getInstance(context).cancelUniqueWork("care-$id")
            context.getSystemService(NotificationManager::class.java).cancel(id.hashCode())
        }
    }

    fun snooze(id: String, title: String, body: String, minutes: Long = 10) {
        require(minutes in 1..1440)
        schedule(id, title, body, System.currentTimeMillis() + minutes * 60_000)
    }

    /**
     * Remove app-scoped WorkManager payloads containing pet names and care notes on data deletion.
     */
    suspend fun deleteAllData() =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val manager = WorkManager.getInstance(context)
            synchronized(lock) {
                check(generations(context).edit().clear().commit()) {
                    "Could not clear reminder generations"
                }
                context.getSystemService(NotificationManager::class.java).cancelAll()
            }
            manager.cancelAllWork().result.get(30, TimeUnit.SECONDS)
            manager.pruneWork().result.get(30, TimeUnit.SECONDS)
            Unit
        }

    companion object {
        private val lock = Any()

        private fun generations(context: Context) =
            context.getSharedPreferences("reminder-generations", Context.MODE_PRIVATE)

        internal fun isCurrent(context: Context, id: String, data: Data): Boolean =
            data.getString("generation") != null &&
                generations(context).getString(id.toString(), null) == data.getString("generation")

        internal fun runIfCurrent(context: Context, id: String, data: Data, action: () -> Unit) {
            synchronized(lock) { if (isCurrent(context, id, data)) action() }
        }

        internal fun enqueue(context: Context, id: String, data: Data, at: Long) {
            synchronized(lock) {
                if (!isCurrent(context, id, data)) return
                val request =
                    OneTimeWorkRequestBuilder<CareReminderWorker>()
                        .setInputData(data)
                        .setInitialDelay(
                            (at - System.currentTimeMillis()).coerceAtLeast(0),
                            TimeUnit.MILLISECONDS,
                        )
                        .build()
                WorkManager.getInstance(context)
                    .enqueueUniqueWork("care-$id", ExistingWorkPolicy.REPLACE, request)
            }
        }
    }
}

class CareReminderWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        val settings = ReminderPreferences(applicationContext)
        val id = inputData.getString("id") ?: return Result.failure()
        if (!ReminderScheduler.isCurrent(applicationContext, id, inputData) || isStopped)
            return Result.success()
        val now = java.time.ZonedDateTime.now()
        if (System.currentTimeMillis() > inputData.getLong("end", Long.MAX_VALUE))
            return Result.success()
        if (
            settings.quietEnabled &&
                QuietHours.contains(now.hour, settings.quietStart, settings.quietEnd)
        ) {
            var deferred = now.with(LocalTime.of(settings.quietEnd, 0))
            if (!deferred.isAfter(now)) deferred = deferred.plusDays(1)
            ReminderScheduler.enqueue(
                applicationContext,
                id,
                inputData,
                deferred.toInstant().toEpochMilli(),
            )
            return Result.success()
        }
        val allowed =
            Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
        if (
            settings.enabled &&
                allowed &&
                NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()
        ) {
            val title = inputData.getString("title") ?: "Pawverio"
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    "care",
                    applicationContext
                        .createConfigurationContext(
                            Configuration(applicationContext.resources.configuration).apply {
                                setLocale(Locale.forLanguageTag(settings.localeTag))
                            }
                        )
                        .getString(R.string.care_channel),
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            )
            val launch =
                applicationContext.packageManager.getLaunchIntentForPackage(
                    applicationContext.packageName
                )
            val notification =
                NotificationCompat.Builder(applicationContext, "care")
                    .setSmallIcon(R.drawable.ic_care_notification)
                    .setContentTitle(title)
                    .setContentText(inputData.getString("body").orEmpty())
                    .setAutoCancel(true)
            if (launch != null)
                notification.setContentIntent(
                    PendingIntent.getActivity(
                        applicationContext,
                        id.hashCode(),
                        launch,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                )
            ReminderScheduler.runIfCurrent(applicationContext, id, inputData) {
                if (!isStopped)
                    try {
                        manager.notify(id.hashCode(), notification.build())
                    } catch (_: SecurityException) {
                        /* Permission revoked mid-flight. */
                    }
            }
        }
        val days = inputData.getInt("days", 0)
        if (days > 0) {
            val anchor =
                inputData.getLong("anchor", inputData.getLong("due", System.currentTimeMillis()))
            val at = ReminderRecurrence.nextAfter(anchor, days, now.toInstant().toEpochMilli())
            if (at <= inputData.getLong("end", Long.MAX_VALUE)) {
                ReminderScheduler.enqueue(
                    applicationContext,
                    id,
                    Data.Builder().putAll(inputData).putLong("due", at).build(),
                    at,
                )
            }
        }
        return Result.success()
    }
}

object ReminderRecurrence {
    /** A snoozed occurrence never becomes a new medication schedule anchor. */
    fun nextAfter(
        anchorMillis: Long,
        days: Int,
        afterMillis: Long,
        zone: ZoneId = ZoneId.systemDefault(),
    ): Long {
        require(days > 0)
        var next = Instant.ofEpochMilli(anchorMillis).atZone(zone)
        val after = Instant.ofEpochMilli(afterMillis)
        if (next.toInstant() > after) return anchorMillis
        val elapsedDays =
            java.time.temporal.ChronoUnit.DAYS.between(
                next.toLocalDate(),
                after.atZone(zone).toLocalDate(),
            )
        next = next.plusDays(maxOf(1, elapsedDays / days) * days)
        while (next.toInstant() <= after) next = next.plusDays(days.toLong())
        return next.toInstant().toEpochMilli()
    }
}
