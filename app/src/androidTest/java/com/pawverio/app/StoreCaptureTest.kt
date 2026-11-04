package com.pawverio.app

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.pawverio.core.*
import java.io.File
import java.util.Locale
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** QA-only fictional data. Never grants a purchase entitlement or ships a demo workflow. */
class StoreCaptureTest {
    @get:Rule val compose = createEmptyComposeRule()

    @Test
    fun captureLocalizedImplementedScreens() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val args = InstrumentationRegistry.getArguments()
        val locales = (args.getString("locales") ?: "en-GB").split(",")
        val variant = args.getString("variant") ?: "fire-tablet"
        context
            .getSharedPreferences("qa-capture", 0)
            .edit()
            .putBoolean("phone", variant == "phone-layout")
            .commit()
        val repo = CareRepository(PawverioDatabase.open(context))
        val settings = Settings(context)
        val expectations =
            org.json.JSONObject(
                instrumentation.context.assets
                    .open("locale-expectations.json")
                    .bufferedReader()
                    .use { it.readText() }
            )
        try {
            locales.forEach { tag ->
                val config =
                    Configuration(context.resources.configuration).apply {
                        setLocale(Locale.forLanguageTag(tag))
                    }
                val loc = context.createConfigurationContext(config)
                fun label(id: Int): String {
                    val actual = loc.getString(id)
                    val key = loc.resources.getResourceEntryName(id)
                    if (expectations.getJSONObject(tag).has(key))
                        assertEquals(
                            "$tag resource must match independent translated catalog",
                            expectations.getJSONObject(tag).getString(key),
                            actual,
                        )
                    return actual
                }
                listOf(
                        R.string.nav_today,
                        R.string.nav_pets,
                        R.string.nav_health,
                        R.string.voice_title,
                        R.string.sitter_mode,
                        R.string.privacy_body,
                    )
                    .forEach { label(it) }
                val now = System.currentTimeMillis()
                runBlocking {
                    repo.deleteAll()
                    settings.set("onboarded", true)
                    settings.set("language", tag)
                    settings.set("spoken", false)
                    repo.savePet(
                        Pet(
                            id = "qa-bella",
                            name = "Bella",
                            species = "dog",
                            breed = label(R.string.dog),
                            weightKg = 12.8,
                            notes = label(R.string.sitter_instructions),
                        )
                    )
                    repo.savePet(
                        Pet(
                            id = "qa-luna",
                            name = "Luna",
                            species = "cat",
                            breed = label(R.string.cat),
                            weightKg = 4.2,
                        )
                    )
                    listOf(
                            CareKind.FEEDING to R.string.feeding,
                            CareKind.WALKING to R.string.walking,
                            CareKind.MEDICATION to R.string.medication,
                            CareKind.GROOMING to R.string.grooming,
                        )
                        .forEachIndexed { i, pair ->
                            repo.saveTask(
                                CareTask(
                                    id = "qa-task-$i",
                                    petId = "qa-bella",
                                    kind = pair.first,
                                    title = label(pair.second),
                                    time = now + 3600000 * (i + 1),
                                    recurrenceDays = 1,
                                    remindersEnabled = false,
                                )
                            )
                        }
                    repo.saveHealthRecord(
                        HealthRecord(
                            id = "qa-vet",
                            petId = "qa-bella",
                            title = label(R.string.vet),
                            provider = label(R.string.clinic),
                            date = now - 86400000,
                        )
                    )
                    repo.saveHealthRecord(
                        HealthRecord(
                            id = "qa-vaccine",
                            petId = "qa-luna",
                            kind = CareKind.VACCINATION,
                            title = label(R.string.vaccination),
                            date = now - 86400000 * 3,
                        )
                    )
                    repo.saveWeight(
                        WeightEntry(petId = "qa-bella", weightKg = 12.2, date = now - 86400000 * 30)
                    )
                    repo.saveWeight(WeightEntry(petId = "qa-bella", weightKg = 12.8, date = now))
                    settings.set("instructions", label(R.string.sitter_instructions))
                    settings.set("emergency", label(R.string.emergency_contact))
                }
                for (route in 0..7) {
                    val scenario =
                        ActivityScenario.launch<MainActivity>(
                            Intent(context, MainActivity::class.java)
                                .putExtra("qa_route", route)
                                .putExtra("qa_capture", true)
                        )
                    try {
                        instrumentation.waitForIdleSync()
                        Thread.sleep(400)
                        instrumentation.waitForIdleSync()
                        val expected =
                            label(
                                when (route) {
                                    2 -> R.string.nav_health
                                    5 -> R.string.voice_title
                                    6 -> R.string.nav_pets
                                    7 -> R.string.sitter_mode
                                    else -> R.string.nav_today
                                }
                            )
                        try {
                            compose.waitUntil(20000) {
                                runCatching {
                                        compose
                                            .onAllNodesWithText(expected)
                                            .fetchSemanticsNodes()
                                            .isNotEmpty()
                                    }
                                    .getOrDefault(false)
                            }
                        } catch (error: Throwable) {
                            val folder =
                                File(context.getExternalFilesDir(null), "capture-diagnostics")
                                    .apply { mkdirs() }
                            File(folder, "$tag-$route.txt")
                                .writeText(
                                    runCatching {
                                            compose
                                                .onAllNodes(isRoot())
                                                .fetchSemanticsNodes()
                                                .indices
                                                .joinToString("\n") {
                                                    compose.onAllNodes(isRoot())[it].printToString()
                                                }
                                        }
                                        .getOrElse { "No active Compose root: ${it.message}" }
                                )
                            val fd =
                                instrumentation.uiAutomation.executeShellCommand("screencap -p")
                            android.os.ParcelFileDescriptor.AutoCloseInputStream(fd).use { input ->
                                File(folder, "$tag-$route.png").outputStream().use {
                                    input.copyTo(it)
                                }
                            }
                            throw IllegalStateException(
                                "Expected actual $tag screen $route label '$expected'; diagnostic saved",
                                error,
                            )
                        }
                        compose.waitForIdle()
                        Thread.sleep(500)
                        val focusDescriptor=instrumentation.uiAutomation.executeShellCommand("dumpsys window")
                        val focus=android.os.ParcelFileDescriptor.AutoCloseInputStream(focusDescriptor).bufferedReader().use{it.readText()}.lineSequence().firstOrNull{it.contains("mCurrentFocus")}.orEmpty()
                        assertTrue("Pawverio must own the displayed window; unlock the tablet and close overlays",focus.contains("com.pawverio.app"))
                        var image = instrumentation.uiAutomation.takeScreenshot()
                        assertNotNull(image)
                        fun colorCount(bitmap: Bitmap): Int {
                            val colors = mutableSetOf<Int>()
                            for (x in 0 until bitmap.width step 30) for (y in
                                0 until bitmap.height step 30) colors.add(bitmap.getPixel(x, y))
                            return colors.size
                        }
                        // Some Fire OS builds return an empty UiAutomation screenshot. The
                        // shell capture still reads the actual displayed app, never a mock.
                        if (colorCount(image) <= 12) {
                            image.recycle()
                            val descriptor =
                                instrumentation.uiAutomation.executeShellCommand("screencap -p")
                            image =
                                android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor)
                                    .use {
                                        requireNotNull(
                                            android.graphics.BitmapFactory.decodeStream(it)
                                        )
                                    }
                        }
                        val header =
                            compose
                                .onAllNodesWithText("Pawverio")
                                .fetchSemanticsNodes()
                                .first()
                                .boundsInWindow
                        fun visibleHeader(bitmap: Bitmap): Boolean {
                            if (route == 5 || route == 7) {
                                var dialogPixels = 0
                                for (x in
                                    bitmap.width * 3 / 10 until
                                        bitmap.width * 7 / 10 step
                                        8) for (y in
                                    bitmap.height * 3 / 10 until bitmap.height * 7 / 10 step 8) {
                                    val pixel = bitmap.getPixel(x, y)
                                    if (
                                        android.graphics.Color.red(pixel) == 229 &&
                                            android.graphics.Color.green(pixel) == 235 &&
                                            android.graphics.Color.blue(pixel) == 222
                                    )
                                        dialogPixels++
                                }
                                return dialogPixels > 25
                            }
                            val colors = mutableSetOf<Int>()
                            val left = header.left.toInt().coerceIn(0, bitmap.width - 1)
                            val right = header.right.toInt().coerceIn(left + 1, bitmap.width)
                            val top = header.top.toInt().coerceIn(0, bitmap.height - 1)
                            val bottom = header.bottom.toInt().coerceIn(top + 1, bitmap.height)
                            for (x in left until right step 2) for (y in
                                top until bottom step 2) colors.add(bitmap.getPixel(x, y))
                            return colors.size > 12
                        }
                        // Semantics can be ready before Fire's compositor replaces the first
                        // onboarding frame. Verify actual app-header pixels, not just nodes.
                        repeat(4) {
                            if (!visibleHeader(image)) {
                                image.recycle()
                                Thread.sleep(600)
                                val descriptor =
                                    instrumentation.uiAutomation.executeShellCommand("screencap -p")
                                image =
                                    android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor)
                                        .use {
                                            requireNotNull(
                                                android.graphics.BitmapFactory.decodeStream(it)
                                            )
                                        }
                            }
                        }
                        assertTrue(
                            "Actual app header is rendered; no stale onboarding frame",
                            visibleHeader(image),
                        )
                        assertTrue("Capture is portrait", image.height > image.width)
                        if (variant == "phone-layout") {
                            val viewport =
                                compose
                                    .onNodeWithTag("qa-phone-viewport")
                                    .fetchSemanticsNode()
                                    .boundsInWindow
                            val crop =
                                Bitmap.createBitmap(
                                    image,
                                    viewport.left.toInt(),
                                    viewport.top.toInt(),
                                    viewport.width.toInt(),
                                    viewport.height.toInt(),
                                )
                            image.recycle()
                            image = crop
                            assertEquals(720, image.width)
                            assertEquals(1280, image.height)
                        }
                        println(
                            "CAPTURE $variant/$tag/${route+1} ${image.width}x${image.height} colors=${colorCount(image)}"
                        )
                        assertTrue(
                            "Capture contains rendered interface rather than blank screen",
                            colorCount(image) > 12,
                        )
                        val directory =
                            File(context.getExternalFilesDir(null), "screenshots/$variant/$tag")
                                .apply { mkdirs() }
                        File(directory, "${route+1}.png").outputStream().use {
                            image.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                        image.recycle()
                    } finally {
                        scenario.close()
                    }
                }
            }
        } finally {
            context.getSharedPreferences("qa-capture", 0).edit().putBoolean("phone", false).commit()
            runBlocking {
                repo.deleteAll()
                settings.set("onboarded", false)
                settings.set("language", "")
                settings.set("spoken", true)
                settings.set("instructions", "")
                settings.set("emergency", "")
            }
        }
    }
}
