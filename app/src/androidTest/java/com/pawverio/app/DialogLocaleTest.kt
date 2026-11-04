package com.pawverio.app

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

/** Regression: Android dialog windows must not replace the selected app locale. */
class DialogLocaleTest {
    @get:Rule val compose = createEmptyComposeRule()

    @Test
    fun spanishVoiceDialogDoesNotFallBackToEnglishDeviceLanguage() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val fixture =
            org.json.JSONObject(
                instrumentation.context.assets
                    .open("locale-expectations.json")
                    .bufferedReader()
                    .use { it.readText() }
            )
        val expected = fixture.getJSONObject("es-ES").getString("voice_title")
        runBlocking {
            Settings(context).set("onboarded", true)
            Settings(context).set("language", "es-ES")
            Settings(context).set("spoken", false)
        }
        val scenario =
            ActivityScenario.launch<MainActivity>(
                Intent(context, MainActivity::class.java).putExtra("qa_route", 5)
            )
        try {
            compose.waitUntil(15000) {
                compose.onAllNodesWithText(expected).fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText(expected).assertExists()
            compose.onNodeWithText("Care with your voice").assertDoesNotExist()
        } finally {
            scenario.close()
            runBlocking {
                Settings(context).set("language", "")
                Settings(context).set("spoken", true)
                Settings(context).set("onboarded", false)
            }
        }
    }
}
