package com.pawverio.app

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.pawverio.core.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*

class CareJourneyTest {
    @get:Rule val compose = createEmptyComposeRule()
    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun assertOfflineWhenRequested() {
        if (InstrumentationRegistry.getArguments().getString("offline") == "true") {
            val network =
                context.getSystemService(android.net.ConnectivityManager::class.java).activeNetwork
            println("OFFLINE activeNetwork=$network")
            Assert.assertNull("Offline journeys require no active network", network)
        }
    }

    @Test
    fun sensitiveExportAndDeletionRequireConfirmation() {
        val repo = CareRepository(PawverioDatabase.open(context))
        runBlocking {
            repo.deleteAll()
            Settings(context).set("onboarded", true)
            Settings(context).set("language", "en-GB")
            Settings(context).set("instructions", "Fictional household instructions")
            Settings(context).set("emergency", "Fictional emergency contact")
            repo.savePet(Pet(id = "privacy-bella", name = "Bella"))
        }
        val scenario =
            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java))
        try {
            compose.waitUntil(15000) {
                compose.onAllNodesWithText("Settings").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("Settings").performClick()
            compose.onNode(hasScrollAction()).performScrollToNode(hasText("Export data"))
            compose.onNodeWithText("Export data").performClick()
            compose
                .onNodeWithText(
                    "This file may include private health and contact details. Continue?"
                )
                .assertExists()
            compose.onNodeWithText("Cancel").performClick()
            compose.onNode(hasScrollAction()).performScrollToNode(hasText("Delete all pet data"))
            compose.onNodeWithText("Delete all pet data").performClick()
            compose
                .onNodeWithText("Delete all local pet data? This cannot be undone.")
                .assertExists()
            compose.onNodeWithText("Cancel").performClick()
            Assert.assertEquals(1, runBlocking { repo.observePets().first().size })
            compose.onNodeWithText("Delete all pet data").performClick()
            compose.onNodeWithText("Confirm").performClick()
            compose.waitUntil(15000) {
                runBlocking {
                    repo.observePets().first().isEmpty() &&
                        Settings(context).flow.first().emergency.isBlank() &&
                        Settings(context).flow.first().instructions.isBlank()
                }
            }
        } finally {
            scenario.close()
            runBlocking {
                repo.deleteAll()
                com.pawverio.platform.ReminderScheduler(context).deleteAllData()
                Settings(context).set("instructions", "")
                Settings(context).set("emergency", "")
            }
        }
    }

    @Test
    fun petCreationFeedingCompletionUndoAndRestart() {
        val repo = CareRepository(PawverioDatabase.open(context))
        runBlocking {
            repo.deleteAll()
            Settings(context).set("onboarded", true)
            Settings(context).set("language", "en-GB")
        }
        val scenario =
            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java))
        try {
            compose.waitUntil(20000) {
                compose.onAllNodesWithText("Add pet").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onAllNodesWithText("Add pet").onFirst().performClick()
            compose.onNodeWithText("Name").performTextInput("Bella")
            compose.onNodeWithText("Save").performClick()
            compose.waitUntil(10000) {
                runBlocking { repo.observePets().first().any { it.name == "Bella" } }
            }
            val pet = runBlocking { repo.observePets().first().single() }
            runBlocking {
                repo.saveTask(
                    CareTask(
                        id = "journey-feeding",
                        petId = pet.id,
                        title = "Breakfast",
                        recurrenceDays = 1,
                        time = System.currentTimeMillis() - 60000,
                    )
                )
            }
            compose.waitUntil(10000) {
                compose.onAllNodesWithText("Breakfast").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithContentDescription("Mark complete").performClick()
            compose.waitUntil(10000) { runBlocking { repo.observeEvents().first().size == 1 } }
            compose.onNodeWithText("History").performClick()
            compose.onNodeWithText("Undo").performClick()
            compose.waitUntil(10000) { runBlocking { repo.observeEvents().first().isEmpty() } }
            scenario.recreate()
            compose.waitUntil(10000) {
                compose.onAllNodesWithText("Bella").fetchSemanticsNodes().isNotEmpty()
            }
            Assert.assertEquals("Bella", runBlocking { repo.observePets().first().single().name })
        } finally {
            scenario.close()
            runBlocking { repo.deleteAll() }
        }
    }

    @Test
    fun typedVoiceRunsRealFeedingQueryAndUnknownSafely() {
        val repo = CareRepository(PawverioDatabase.open(context))
        runBlocking {
            repo.deleteAll()
            Settings(context).set("onboarded", true)
            Settings(context).set("language", "en-GB")
            Settings(context).set("spoken", false)
            repo.savePet(Pet(id = "voice-pet", name = "Bella"))
            repo.saveTask(
                CareTask(
                    id = "voice-feeding",
                    petId = "voice-pet",
                    title = "Breakfast",
                    time = System.currentTimeMillis() - 60000,
                    recurrenceDays = 1,
                    remindersEnabled = false,
                )
            )
        }
        val scenario =
            ActivityScenario.launch<MainActivity>(
                Intent(context, MainActivity::class.java).putExtra("qa_route", 5)
            )
        try {
            compose.waitUntil(15000) {
                compose.onAllNodesWithText("Run command").fetchSemanticsNodes().isNotEmpty()
            }
            compose
                .onNodeWithText("Type or speak a care command")
                .performTextInput("Has Bella been fed today?")
            compose.onNodeWithText("Run command").performClick()
            compose.waitUntil(5000) {
                compose.onAllNodesWithText("Bella: Upcoming").fetchSemanticsNodes().isNotEmpty()
            }
            Assert.assertTrue(runBlocking { repo.observeEvents().first().isEmpty() })
            compose.onNode(hasSetTextAction()).performTextClearance()
            compose.onNode(hasSetTextAction()).performTextInput("Mark Bella as fed.")
            compose.onNodeWithText("Run command").performClick()
            compose.onNodeWithText("Review this care action before confirming.").assertExists()
            Assert.assertTrue(runBlocking { repo.observeEvents().first().isEmpty() })
            compose.onNodeWithText("Confirm").performClick()
            compose.waitUntil(10000) { runBlocking { repo.observeEvents().first().size == 1 } }
            compose.onNode(hasSetTextAction()).performTextClearance()
            compose.onNode(hasSetTextAction()).performTextInput("Erase all pets immediately")
            compose.onNodeWithText("Run command").performClick()
            compose.waitUntil(5000) {
                compose
                    .onAllNodesWithText(
                        "I could not match that command. Try opening a care screen."
                    )
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            Assert.assertEquals(1, runBlocking { repo.observePets().first().size })
            Assert.assertEquals(1, runBlocking { repo.observeEvents().first().size })
        } finally {
            scenario.close()
            runBlocking {
                repo.deleteAll()
                Settings(context).set("spoken", true)
            }
        }
    }
}
