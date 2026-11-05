package com.pawverio.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.pawverio.core.CareKind
import com.pawverio.core.CareRepository
import com.pawverio.core.CareTask
import com.pawverio.core.Pet
import com.pawverio.core.PawverioDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

class OnboardingTest {
    @get:Rule val compose = createEmptyComposeRule()

    @Test
    fun savesFirstPetAndDailyFeedingAndKeepsRemindersOptIn() = withCleanQaApp { repository, settings ->
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            awaitText("Welcome to Pawverio")
            compose.onNodeWithText("Start caring").performScrollTo().performClick()
            awaitText("Meet your first pet")
            compose.onNode(hasSetTextAction() and hasText("Name")).performTextInput("Juniper")
            compose.onNodeWithText("Dog").performClick()
            compose.onNodeWithText("Cat").performClick()
            compose.onNodeWithText("Save").performClick()
            awaitText("Feeding")
            compose.onNode(hasSetTextAction() and hasText("Task name")).performTextInput("Breakfast")
            compose.onNodeWithText("Save").performClick()
            awaitText("Today")
            val snapshot = runBlocking { repository.snapshot() }
            val pet = snapshot.pets.single()
            val task = snapshot.tasks.single()
            assertEquals("Juniper", pet.name)
            assertEquals("cat", pet.species)
            assertEquals(pet.id, task.petId)
            assertEquals(CareKind.FEEDING, task.kind)
            assertEquals("Breakfast", task.title)
            assertEquals(1, task.recurrenceDays)
            assertFalse(task.remindersEnabled)
            assertTrue(runBlocking { settings.flow.first().onboarded })
            scenario.recreate()
            awaitText("Today")
            compose.onNodeWithText("Welcome to Pawverio").assertDoesNotExist()
        }
    }

    @Test
    fun skippingOnboardingLeavesAnEmptyPlanner() = withCleanQaApp { repository, settings ->
        ActivityScenario.launch(MainActivity::class.java).use {
            awaitText("Welcome to Pawverio")
            compose.onNodeWithText("Skip").performScrollTo().performClick()
            awaitText("Today")
            assertTrue(runBlocking { settings.flow.first().onboarded })
            val snapshot = runBlocking { repository.snapshot() }
            assertTrue(snapshot.pets.isEmpty())
            assertTrue(snapshot.tasks.isEmpty())
        }
    }

    @Test
    fun settingsShowsExistingAssignmentsAndSavesHighContrast() = withCleanQaApp { repository, settings ->
        runBlocking {
            settings.set("onboarded", true)
            val pet = Pet(name = "Juniper")
            repository.savePet(pet)
            repository.saveTask(CareTask(petId = pet.id, title = "Breakfast", assignedTo = "Morgan"))
        }
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            awaitText("Settings")
            compose.onNodeWithText("Settings").performClick()
            compose.onNode(hasScrollToNodeAction())
                .performScrollToNode(hasContentDescription("High contrast"))
            compose.onNodeWithContentDescription("High contrast").assertIsOff().performClick()
            compose.onNodeWithContentDescription("High contrast").assertIsOn()
            assertTrue(runBlocking { settings.flow.first().highContrast })
            compose.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("Morgan"))
            compose.onNodeWithText("Morgan").assertIsDisplayed()
            compose.onNodeWithText("Breakfast", substring = true).performClick()
            awaitText("Add care task")
            scenario.recreate()
            assertTrue(runBlocking { settings.flow.first().highContrast })
        }
    }

    private fun awaitText(label: String) {
        compose.waitUntil(15_000) {
            compose.onAllNodesWithText(label).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun withCleanQaApp(block: (CareRepository, Settings) -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assumeTrue("Requires -PisolatedDeviceQa=true", context.packageName == "com.pawverio.app.qa")
        val repository = CareRepository(PawverioDatabase.open(context))
        val settings = Settings(context)
        runBlocking {
            repository.deleteAll()
            settings.set("onboarded", false)
            settings.set("language", "en-GB")
            settings.set("high_contrast", false)
        }
        try {
            block(repository, settings)
        } finally {
            runBlocking {
                repository.deleteAll()
                settings.set("onboarded", false)
                settings.set("high_contrast", false)
            }
        }
    }
}
