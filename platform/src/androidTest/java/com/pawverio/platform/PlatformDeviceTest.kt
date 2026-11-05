package com.pawverio.platform

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.graphics.pdf.PdfRenderer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkManager
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ExportTestProvider : ContentProvider() {
    override fun onCreate() = true
    override fun getType(uri: Uri) = "application/octet-stream"
    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0
    override fun openFile(uri: Uri,mode: String): ParcelFileDescriptor {
        val name = requireNotNull(uri.lastPathSegment)
        require(name.matches(Regex("[a-z.]+")))
        return ParcelFileDescriptor.open(File(requireNotNull(context).cacheDir,name),ParcelFileDescriptor.parseMode(mode))
    }
}

@RunWith(AndroidJUnit4::class)
class PlatformDeviceTest {
    @Test fun reminderLanguageSupportsManualChoiceAndTracksAutomaticDeviceLanguage(){
        val settings=ReminderPreferences(context)
        val previous=settings.localeTag
        val systemLocale=Locale.getDefault()
        try{
            settings.localeTag="fr-FR";assertEquals("fr-FR",settings.localeTag)
            Locale.setDefault(Locale.US);settings.localeTag="";assertEquals("en-US",settings.localeTag)
            Locale.setDefault(Locale.GERMANY);assertEquals("de-DE",settings.localeTag)
        }finally{Locale.setDefault(systemLocale);settings.localeTag=previous}
    }
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test fun deviceVoiceCapabilityAndPermissionFallback() {
        println("DEVICE model=${android.os.Build.MODEL}; api=${android.os.Build.VERSION.SDK_INT}; recognitionAvailable=${SpeechRecognizer.isRecognitionAvailable(context)}")
        val states = mutableListOf<VoiceState>()
        instrumentation.runOnMainSync {
            val controller = VoiceController(context,{},states::add)
            controller.start("en-GB")
            assertTrue(states.last() == VoiceState.PERMISSION_REQUIRED || states.last() == VoiceState.UNAVAILABLE)
            controller.close()
        }
    }

    @Test fun ttsLocaleCapabilityReport() {
        val latch = CountDownLatch(1)
        var initStatus = -1
        var tts: TextToSpeech? = null
        instrumentation.runOnMainSync { tts = TextToSpeech(context) { initStatus = it; latch.countDown() } }
        assertTrue("TTS initialization callback arrived",latch.await(30,TimeUnit.SECONDS))
        println("TTS initialization=$initStatus")
        val locales = listOf("en-GB","en-US","en-AU","es-ES","es-419","fr-FR","fr-CA","de-DE","it-IT","pt-BR","pt-PT","nl-NL","pl-PL","sv-SE","ja-JP","hi-IN","ar","tr-TR","id-ID")
        locales.forEach { tag -> println("TTS $tag availability=${tts?.isLanguageAvailable(Locale.forLanguageTag(tag))}") }
        instrumentation.runOnMainSync { tts?.shutdown() }
    }

    @Test fun englishVoiceEngineProducesAudio() {
        val ready = CountDownLatch(1)
        val finished = CountDownLatch(1)
        var initStatus = TextToSpeech.ERROR
        var synthStatus = TextToSpeech.ERROR
        val output = File.createTempFile("pawverio-speech-", ".wav", context.cacheDir)
        val tts = TextToSpeech(context) { status ->
            initStatus = status
            ready.countDown()
        }
        try {
            assertTrue("TTS initialization callback arrived", ready.await(30, TimeUnit.SECONDS))
            assertEquals(TextToSpeech.SUCCESS, initStatus)
            assertTrue(tts.setLanguage(Locale.UK) >= TextToSpeech.LANG_AVAILABLE)
            tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) = Unit
                override fun onDone(utteranceId: String?) {
                    synthStatus = TextToSpeech.SUCCESS
                    finished.countDown()
                }
                override fun onError(utteranceId: String?) {
                    finished.countDown()
                }
            })
            assertEquals(
                TextToSpeech.SUCCESS,
                tts.synthesizeToFile("Bella has a care reminder.", android.os.Bundle(), output, "pawverio-qa"),
            )
            assertTrue("Synthesis completion callback arrived", finished.await(30, TimeUnit.SECONDS))
            assertEquals(TextToSpeech.SUCCESS, synthStatus)
            val extractor = android.media.MediaExtractor()
            try {
                extractor.setDataSource(output.absolutePath)
                val audioTrack = (0 until extractor.trackCount).first {
                    extractor.getTrackFormat(it).getString(android.media.MediaFormat.KEY_MIME)
                        ?.startsWith("audio/") == true
                }
                extractor.selectTrack(audioTrack)
                assertTrue("Synthesized audio contains samples", extractor.readSampleData(java.nio.ByteBuffer.allocate(65536), 0) > 0)
            } finally {
                extractor.release()
            }
        } finally {
            tts.shutdown()
            output.delete()
        }
    }

    @Test fun exportsWriteActualUtf8TextAndReadableMultipagePdf() {
        val service = ExportService(context)
        val textUri = Uri.parse("content://com.pawverio.platform.test.exports/report.txt")
        val text = "Bella — care instructions\n日本語\nالعربية"
        service.writeText(textUri,text)
        assertEquals(text,context.contentResolver.openInputStream(textUri)!!.bufferedReader().use { it.readText() })
        val pdfUri = Uri.parse("content://com.pawverio.platform.test.exports/report.pdf")
        service.writePdf(pdfUri,"Pawverio sitter report",(1..140).map { "Task $it: Bella's feeding and walking instructions." })
        val descriptor = context.contentResolver.openFileDescriptor(pdfUri,"r")!!
        PdfRenderer(descriptor).use { renderer ->
            assertTrue(renderer.pageCount >= 3)
            renderer.openPage(0).use { assertEquals(595,it.width); assertEquals(842,it.height) }
            println("PDF pages=${renderer.pageCount}")
        }
    }

    @Test fun authenticatedBackupRoundTripOnFireRuntime() {
        val bytes = "Bella: medication instructions — 日本語".toByteArray()
        val encrypted = EncryptedBackup.encrypt(bytes,"correct horse battery staple".toCharArray())
        assertArrayEquals(bytes,EncryptedBackup.decrypt(encrypted,"correct horse battery staple".toCharArray()))
        assertThrows(Exception::class.java) { EncryptedBackup.decrypt(encrypted,"wrong password provided".toCharArray()) }
    }

    @Test fun scheduledWorkPersistsAndCancelledGenerationCannotRevive() {
        val scheduler = ReminderScheduler(context)
        val id = "instrumentation-reminder"
        val due = System.currentTimeMillis() + 3_600_000
        scheduler.schedule(id,"Care reminder","Fictional test task",due,1)
        val manager = WorkManager.getInstance(context)
        val first = manager.getWorkInfosForUniqueWork("care-$id").get(20,TimeUnit.SECONDS)
        assertEquals(1,first.count { !it.state.isFinished })
        val oldToken = context.getSharedPreferences("reminder-generations",0).getString(id,null)
        assertNotNull(oldToken)
        scheduler.schedule(id,"Care reminder","Fictional test task",due,1)
        val unchanged = manager.getWorkInfosForUniqueWork("care-$id").get(20,TimeUnit.SECONDS)
        assertEquals("Unchanged schedule preserves WorkManager request",first.first().id,unchanged.first().id)
        assertEquals("Unchanged schedule preserves generation",oldToken,context.getSharedPreferences("reminder-generations",0).getString(id,null))
        scheduler.cancel(id)
        val data = androidx.work.Data.Builder().putString("id",id).putString("generation",oldToken).build()
        assertFalse(ReminderScheduler.isCurrent(context,id,data))
        ReminderScheduler.enqueue(context,id,data,System.currentTimeMillis())
        val after = manager.getWorkInfosForUniqueWork("care-$id").get(20,TimeUnit.SECONDS)
        assertTrue(after.all { it.state.isFinished })
    }

    @Test fun disabledNotificationsWorkerCompletesWithoutPosting() = runBlocking {
        val prefs = ReminderPreferences(context)
        val previouslyEnabled = prefs.enabled
        val wasQuiet = prefs.quietEnabled
        prefs.enabled = false; prefs.quietEnabled = false
        val id = "disabled-notification-test"
        val generation = "test-generation"
        context.getSharedPreferences("reminder-generations",0).edit().putString(id,generation).commit()
        val manager = context.getSystemService(android.app.NotificationManager::class.java)
        manager.cancel(id.hashCode())
        try {
            val data = androidx.work.Data.Builder().putString("id",id).putString("generation",generation)
                .putString("title","Fictional care task").putInt("days",0).build()
            val worker = TestListenableWorkerBuilder<CareReminderWorker>(context).setInputData(data).build()
            assertEquals(androidx.work.ListenableWorker.Result.success(),worker.doWork())
            assertFalse(manager.activeNotifications.any { it.id == id.hashCode() })
        } finally {
            prefs.enabled = previouslyEnabled; prefs.quietEnabled = wasQuiet
            ReminderScheduler(context).cancel(id)
        }
    }

    @Test fun realNotificationPostsAndCancellationDismissesIt() = runBlocking {
        val prefs = ReminderPreferences(context)
        val previouslyEnabled = prefs.enabled
        val wasQuiet = prefs.quietEnabled
        prefs.enabled = true; prefs.quietEnabled = false
        val id = "delivery-notification-test"
        val generation = "delivery-generation"
        context.getSharedPreferences("reminder-generations",0).edit().putString(id,generation).commit()
        val manager = context.getSystemService(android.app.NotificationManager::class.java)
        manager.cancel(id.hashCode())
        try {
            // This acceptance check needs notifications enabled on the real device. A denial is not a pass.
            assertTrue("Notifications must be enabled to verify delivery",manager.areNotificationsEnabled())
            val data = androidx.work.Data.Builder().putString("id",id).putString("generation",generation)
                .putString("title","Pawverio test reminder").putString("body","Fictional test task").putInt("days",0).build()
            val worker = TestListenableWorkerBuilder<CareReminderWorker>(context).setInputData(data).build()
            assertEquals(androidx.work.ListenableWorker.Result.success(),worker.doWork())
            repeat(40) { if (manager.activeNotifications.none { it.id == id.hashCode() }) kotlinx.coroutines.delay(50) }
            val posted = manager.activeNotifications.firstOrNull { it.id == id.hashCode() }
            assertNotNull("Android NotificationManager reports posted reminder",posted)
            assertEquals("Pawverio test reminder",posted!!.notification.extras.getString(android.app.Notification.EXTRA_TITLE))
            ReminderScheduler(context).cancel(id)
            repeat(40) { if (manager.activeNotifications.any { it.id == id.hashCode() }) kotlinx.coroutines.delay(50) }
            assertFalse(manager.activeNotifications.any { it.id == id.hashCode() })
        } finally {
            prefs.enabled = previouslyEnabled; prefs.quietEnabled = wasQuiet
            ReminderScheduler(context).cancel(id)
        }
    }

    @Test fun scheduledReminderPostsThroughWorkManager() = runBlocking {
        val prefs = ReminderPreferences(context)
        val previouslyEnabled = prefs.enabled
        val wasQuiet = prefs.quietEnabled
        val scheduler = ReminderScheduler(context)
        val manager = context.getSystemService(android.app.NotificationManager::class.java)
        val id = "scheduled-delivery-${java.util.UUID.randomUUID()}"
        try {
            assertTrue("Notifications must be enabled to verify delivery", manager.areNotificationsEnabled())
            prefs.enabled = true
            prefs.quietEnabled = false
            val due = System.currentTimeMillis() + 4_000
            scheduler.schedule(id, "Pawverio scheduled test reminder", "Fictional test task", due)
            assertFalse(manager.activeNotifications.any { it.id == id.hashCode() })
            val posted = kotlinx.coroutines.withTimeout(45_000) {
                var notification = manager.activeNotifications.firstOrNull { it.id == id.hashCode() }
                while (notification == null) {
                    kotlinx.coroutines.delay(250)
                    notification = manager.activeNotifications.firstOrNull { it.id == id.hashCode() }
                }
                notification
            }
            assertTrue("Reminder posted before its due time", posted.postTime >= due - 250)
            assertEquals(
                "Pawverio scheduled test reminder",
                posted.notification.extras.getString(android.app.Notification.EXTRA_TITLE),
            )
        } finally {
            scheduler.cancel(id)
            prefs.enabled = previouslyEnabled
            prefs.quietEnabled = wasQuiet
        }
    }

    @Test fun expiredMedicationEndDateSuppressesDeliveryAndRecurrence() = runBlocking {
        val id = "end-date-test"
        val generation = "end-date-generation"
        context.getSharedPreferences("reminder-generations",0).edit().putString(id,generation).commit()
        val manager = context.getSystemService(android.app.NotificationManager::class.java)
        manager.cancel(id.hashCode())
        val data = androidx.work.Data.Builder().putString("id",id).putString("generation",generation)
            .putString("title","Expired fictional medication").putInt("days",1)
            .putLong("due",System.currentTimeMillis()-120_000).putLong("end",System.currentTimeMillis()-60_000).build()
        val worker = TestListenableWorkerBuilder<CareReminderWorker>(context).setInputData(data).build()
        assertEquals(androidx.work.ListenableWorker.Result.success(),worker.doWork())
        assertFalse(manager.activeNotifications.any { it.id == id.hashCode() })
        val work = WorkManager.getInstance(context).getWorkInfosForUniqueWork("care-$id").get(20,TimeUnit.SECONDS)
        assertTrue("Expired medication cannot enqueue another occurrence",work.all { it.state.isFinished })
        ReminderScheduler(context).cancel(id)
    }

    @Test fun deletingAppReminderDataClearsPersistedWorkAndGenerationFingerprints() = runBlocking {
        val scheduler = ReminderScheduler(context)
        val id = "data-deletion-test"
        scheduler.schedule(id,"Fictional private pet","Fictional private care notes",System.currentTimeMillis()+3_600_000,1)
        val manager = WorkManager.getInstance(context)
        assertTrue(manager.getWorkInfosForUniqueWork("care-$id").get(20,TimeUnit.SECONDS).isNotEmpty())
        assertTrue(context.getSharedPreferences("reminder-generations",0).all.isNotEmpty())
        scheduler.deleteAllData()
        assertTrue(manager.getWorkInfosForUniqueWork("care-$id").get(20,TimeUnit.SECONDS).isEmpty())
        assertTrue(context.getSharedPreferences("reminder-generations",0).all.isEmpty())
        assertTrue(context.getSystemService(android.app.NotificationManager::class.java).activeNotifications.isEmpty())
    }

    @Test fun absentAmazonPublicKeyDoesNotStartStoreOrBlockOfflineApplication() {
        val hasKey = context.assets.list("")?.contains("AppstoreAuthenticationKey.pem") == true
        org.junit.Assume.assumeFalse("This negative configuration test requires an unconfigured test target",hasKey)
        run {
            val billing = AmazonBilling(context)
            billing.connect()
            assertEquals(BillingStatus.UNAVAILABLE,billing.state.value.status)
            assertFalse(billing.state.value.busy)
            assertNull(billing.state.value.storePrice)
        }
    }
}
