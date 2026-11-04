package com.pawverio.core

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    @Test fun indonesianCommandsResolveWithTheNativeAndroidLocaleAlias(){
        val locale=java.util.Locale.forLanguageTag("id-ID")
        assertEquals(VoiceIntent.Medications,VoiceIntentParser().parse("Tampilkan pengingat obat",locale,emptyList()))
    }
    private lateinit var context: Context
    private lateinit var db: PawverioDatabase
    private lateinit var repository: CareRepository
    private val file = "pawverio-instrumentation.db"
    private fun open() {
        db = Room.databaseBuilder(context, PawverioDatabase::class.java, file).build()
        repository = CareRepository(db)
    }
    @Before fun setUp() { context = ApplicationProvider.getApplicationContext(); context.deleteDatabase(file); open() }
    @After fun tearDown() { db.close(); context.deleteDatabase(file) }

    @Test fun petsEditPreservesChildrenAndRestartPersistsData() = runBlocking {
        val pet = Pet(id = "bella", name = "Bella", notes = "Sensitive paws")
        repository.savePet(pet)
        repository.saveTask(CareTask(id = "meal", petId = pet.id, title = "Breakfast"))
        repository.savePet(pet.copy(name = "Bella Rose"))
        assertEquals(1, repository.observeTasks().first().size)
        db.close(); open()
        assertEquals("Bella Rose", repository.observePets().first().single().name)
        assertEquals("Breakfast", repository.observeTasks().first().single().title)
        repository.deletePet(pet.id)
        assertTrue(repository.observePets().first().isEmpty())
        assertTrue(repository.observeTasks().first().isEmpty())
    }
    @Test fun feedingHistoryUndoAndDoubleCompletionProtection() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Max"))
        repository.saveTask(CareTask(id = "meal", petId = "p", title = "Breakfast", time = 1_700_000_000_000L, recurrenceDays = 1))
        val event = repository.completeTask("meal", 1_700_000_000_001L)!!
        assertEquals(1, repository.observeEvents().first().size)
        assertTrue(repository.observeTasks().first().single().time > event.completedAt)
        repository.undoCompletion(event.id)
        assertEquals(1_700_000_000_000L, repository.observeTasks().first().single().time)
        assertTrue(repository.observeEvents().first().isEmpty())
        repository.saveTask(CareTask(id = "once", petId = "p", title = "One off"))
        assertNotNull(repository.completeTask("once"))
        assertNull(repository.completeTask("once"))
    }
    @Test fun healthWeightHouseholdWalkAndDeletionPersist() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Luna"))
        repository.saveHealthRecord(HealthRecord(id = "v", petId = "p", kind = CareKind.VACCINATION, title = "User-entered vaccine", provider = "Fictional Clinic"))
        repository.saveWeight(WeightEntry(id = "w", petId = "p", weightKg = 4.2))
        repository.saveMember(HouseholdMember(id = "h", name = "Alex"))
        repository.logWalk("p", 20, 1.2, "Evening walk")
        db.close(); open()
        assertEquals("Fictional Clinic", repository.observeHealthRecords().first().single().provider)
        assertEquals(4.2, repository.observeWeights().first().single().weightKg, 0.0001)
        assertEquals("Alex", repository.observeHousehold().first().single().name)
        assertEquals(20, repository.observeEvents().first().single().durationMinutes)
        repository.deleteAll()
        db.close(); open()
        assertTrue(repository.observePets().first().isEmpty())
        assertTrue(repository.observeHealthRecords().first().isEmpty())
        assertTrue(repository.observeWeights().first().isEmpty())
        assertTrue(repository.observeEvents().first().isEmpty())
        assertTrue(repository.observeHousehold().first().isEmpty())
    }
    @Test fun invalidWeightRejectedWithoutDatabaseWrite() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Luna"))
        try { repository.saveWeight(WeightEntry(petId = "p", weightKg = Double.NaN)); fail("Expected validation failure") } catch (_: IllegalArgumentException) { }
        assertTrue(repository.observeWeights().first().isEmpty())
    }
    @Test fun snoozeDoesNotChangeMedicationSchedule() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Max"))
        val now = System.currentTimeMillis()
        repository.saveTask(CareTask(id = "dose", petId = "p", title = "User prescription", kind = CareKind.MEDICATION, time = now, recurrenceDays = 1, dosage = "As prescribed"))
        repository.snoozeTask("dose", now + TimeUnit.MINUTES.toMillis(20))
        val snoozed = repository.observeTasks().first().single()
        assertEquals(now, snoozed.time)
        assertEquals(now + TimeUnit.MINUTES.toMillis(20), snoozed.snoozedUntil)
        repository.completeTask("dose", now)
        val resumed = repository.observeTasks().first().single()
        assertEquals(Recurrence.nextAfter(now, 1, now), resumed.time)
        assertNull(resumed.snoozedUntil)
        assertEquals("As prescribed", resumed.dosage)
    }
    @Test fun pauseResumeAndTaskDeletePreserveHistory() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Max"))
        repository.saveTask(CareTask(id = "g", petId = "p", title = "Brush", kind = CareKind.GROOMING))
        repository.pauseTask("g", true)
        assertNull(repository.completeTask("g"))
        repository.pauseTask("g", false)
        assertNotNull(repository.completeTask("g"))
        repository.deleteTask("g")
        assertTrue(repository.observeTasks().first().isEmpty())
        assertEquals(CareKind.GROOMING, repository.observeEvents().first().single().kind)
        db.close(); open()
        assertEquals(1, repository.observeEvents().first().size)
    }
    @Test fun healthEditsAndWeightOrderingArePersistent() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Luna"))
        val record = HealthRecord(id = "v", petId = "p", kind = CareKind.VACCINATION, title = "Vaccine", date = 100, nextDueDate = 700, notes = "User-entered dates")
        repository.saveHealthRecord(record)
        repository.saveHealthRecord(record.copy(provider = "Updated clinic"))
        repository.saveWeight(WeightEntry(id = "new", petId = "p", weightKg = 5.1, date = 300))
        repository.saveWeight(WeightEntry(id = "old", petId = "p", weightKg = 5.0, date = 200))
        db.close(); open()
        assertEquals(700L, repository.observeHealthRecords().first().single().nextDueDate)
        assertEquals("Updated clinic", repository.observeHealthRecords().first().single().provider)
        assertEquals(listOf("old", "new"), repository.observeWeights().first().map { it.id })
        repository.deleteHealthRecord("v"); repository.deleteWeight("old")
        db.close(); open()
        assertTrue(repository.observeHealthRecords().first().isEmpty())
        assertEquals(listOf("new"), repository.observeWeights().first().map { it.id })
    }
    @Test fun concurrentCompletionCreatesOnlyOneOccurrence() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Max"))
        repository.saveTask(CareTask(id = "g", petId = "p", title = "Meal", recurrenceDays = 1))
        coroutineScope {
            val first = async { repository.completeTask("g") }
            val second = async { repository.completeTask("g") }
            listOf(first, second).forEach { it.await() }
        }
        assertEquals(1, repository.observeEvents().first().size)
    }
    @Test fun portableBackupMergesAndSurvivesRestart() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Bella"))
        repository.saveTask(CareTask(id = "t", petId = "p", title = "Breakfast"))
        repository.completeTask("t")
        repository.saveHealthRecord(HealthRecord(id = "r", petId = "p", title = "Vet visit", nextDueDate = 700))
        repository.saveWeight(WeightEntry(id = "w", petId = "p", weightKg = 5.0))
        repository.saveMember(HouseholdMember(id = "h", name = "Alex"))
        val backup = BackupCodec.exportJson(repository)
        repository.deleteAll()
        val result = BackupCodec.importJson(repository, backup)
        assertEquals(1, result.pets)
        // A second restore upserts the same IDs instead of duplicating history.
        BackupCodec.importJson(repository, backup)
        db.close(); open()
        assertEquals("Bella", repository.observePets().first().single().name)
        assertEquals(1, repository.observeEvents().first().size)
        assertEquals(700L, repository.observeHealthRecords().first().single().nextDueDate)
        assertEquals(5.0, repository.observeWeights().first().single().weightKg, 0.0)
        assertEquals("Alex", repository.observeHousehold().first().single().name)
    }
    @Test fun invalidBackupLeavesExistingDatabaseUntouched() = runBlocking {
        repository.savePet(Pet(id = "existing", name = "Existing pet"))
        val invalid = org.json.JSONObject(BackupCodec.exportJson(repository))
        invalid.getJSONArray("pets").getJSONObject(0).put("name", "Changed")
        invalid.put("version", 99)
        try { BackupCodec.importJson(repository, invalid.toString()); fail("Expected refusal") } catch (_: IllegalArgumentException) { }
        db.close(); open()
        assertEquals("Existing pet", repository.observePets().first().single().name)
    }
    @Test fun manualWalkDatePersistsAndFutureCompletionIsRejected() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Max"))
        val yesterday = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
        repository.logWalk("p", 25, 1.5, "Yesterday's walk", at = yesterday)
        try { repository.logWalk("p", 10, at = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)); fail("Expected future date refusal") } catch (_: IllegalArgumentException) { }
        db.close(); open()
        assertEquals(yesterday, repository.observeEvents().first().single().completedAt)
    }
    @Test fun medicationDueOutsideUserDatesIsRejectedWithoutChangingPrescription() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Max"))
        val now = System.currentTimeMillis()
        val prescription = CareTask(id = "rx", petId = "p", kind = CareKind.MEDICATION, title = "User prescription", time = now, recurrenceDays = 2, startDate = now - 1000, endDate = now + TimeUnit.DAYS.toMillis(5), dosage = "User-entered dosage")
        repository.saveTask(prescription)
        listOf(prescription.copy(time = now - 2000), prescription.copy(time = now + TimeUnit.DAYS.toMillis(6))).forEach { invalid ->
            try { repository.saveTask(invalid); fail("Expected outside-prescription-date refusal") } catch (_: IllegalArgumentException) { }
        }
        assertEquals(prescription, repository.observeTasks().first().single())
        db.close(); open()
        assertEquals(prescription, repository.observeTasks().first().single())
    }
    @Test fun medicationExpiryStopsAtUserEndWithoutCreatingUnprescribedOccurrenceAndUndoWorks() = runBlocking {
        repository.savePet(Pet(id = "p", name = "Max"))
        val now = System.currentTimeMillis()
        repository.saveTask(CareTask(id = "rx", petId = "p", kind = CareKind.MEDICATION, title = "Final prescribed occurrence", time = now, recurrenceDays = 1, endDate = now + 1000))
        val event = repository.completeTask("rx", now)!!
        val stopped = repository.observeTasks().first().single()
        assertTrue(stopped.paused)
        assertEquals(now, stopped.time)
        // Editing a finished prescription remains possible without moving its dates.
        repository.saveTask(stopped.copy(notes = "Finished"))
        repository.undoCompletion(event.id)
        val undone = repository.observeTasks().first().single()
        assertFalse(undone.paused)
        assertEquals(now, undone.time)
        assertTrue(repository.observeEvents().first().isEmpty())
    }
}

