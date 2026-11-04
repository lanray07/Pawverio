package com.pawverio.core

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class BackupCodecTest {
    private fun snapshot() = BackupSnapshot(
        listOf(Pet(id = "p", name = "Bella", notes = "Sensitive paws", photoPath = "/private/photo.jpg")),
        listOf(CareTask(id = "t", petId = "p", title = "Breakfast", recurrenceDays = 1, portion = "User portion", snoozedUntil = 100L)),
        listOf(CareEvent(id = "e", taskId = "t", petId = "p", kind = CareKind.FEEDING, title = "Breakfast", scheduledAt = 10L)),
        listOf(HealthRecord(id = "r", petId = "p", title = "Clinic visit", nextDueDate = 500L, attachmentPath = "/private/vet.pdf")),
        listOf(WeightEntry(id = "w", petId = "p", weightKg = 4.5)),
        listOf(HouseholdMember(id = "m", name = "Alex", contact = "Emergency contact"))
    )
    @Test fun roundTripPreservesRecordsButNeverImportsFilePaths() {
        val source = snapshot()
        val text = BackupCodec.encode(source)
        assertFalse(text.contains("/private"))
        val restored = BackupCodec.decode(text)
        assertEquals(source.pets.single().copy(photoPath = null), restored.pets.single())
        assertEquals(source.tasks, restored.tasks)
        assertEquals(source.events, restored.events)
        assertEquals(source.records.single().copy(attachmentPath = null), restored.records.single())
        assertEquals(source.weights, restored.weights)
        assertEquals(source.members, restored.members)
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsMissingPet() {
        val json = JSONObject(BackupCodec.encode(snapshot()))
        json.getJSONArray("tasks").getJSONObject(0).put("petId", "missing")
        BackupCodec.decode(json.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsDuplicateIds() {
        val json = JSONObject(BackupCodec.encode(snapshot()))
        val pets = json.getJSONArray("pets")
        pets.put(pets.getJSONObject(0))
        BackupCodec.decode(json.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsUnknownCareKind() {
        val json = JSONObject(BackupCodec.encode(snapshot()))
        json.getJSONArray("tasks").getJSONObject(0).put("kind", "unknown")
        BackupCodec.decode(json.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsNegativeWeight() {
        val json = JSONObject(BackupCodec.encode(snapshot()))
        json.getJSONArray("weights").getJSONObject(0).put("weightKg", -1)
        BackupCodec.decode(json.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsFractionalRecurrence() {
        val json = JSONObject(BackupCodec.encode(snapshot()))
        json.getJSONArray("tasks").getJSONObject(0).put("recurrenceDays", 1.5)
        BackupCodec.decode(json.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsFutureFormat() {
        val json = JSONObject(BackupCodec.encode(snapshot())).put("version", 2)
        BackupCodec.decode(json.toString())
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsOversizedInput() { BackupCodec.decode(" ".repeat(10 * 1024 * 1024 + 1)) }
    @Test(expected = IllegalArgumentException::class) fun rejectsExcessiveNestingBeforeJsonParsing() { BackupCodec.decode("[".repeat(1000) + "]".repeat(1000)) }
    @Test fun foreignPathFieldsCannotAccessLocalStorage() {
        val json = JSONObject(BackupCodec.encode(snapshot()))
        json.getJSONArray("pets").getJSONObject(0).put("photoPath", "../../other-app/secret")
        json.getJSONArray("records").getJSONObject(0).put("attachmentPath", "content://foreign/secret")
        val restored = BackupCodec.decode(json.toString())
        assertNull(restored.pets.single().photoPath)
        assertNull(restored.records.single().attachmentPath)
    }
}
