package com.pawverio.core

import androidx.room.withTransaction
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow

class CareRepository(private val database: PawverioDatabase) {
    private val dao = database.careDao()

    fun observePets(): Flow<List<Pet>> = dao.observePets()

    fun observeTasks(): Flow<List<CareTask>> = dao.observeTasks()

    fun observeEvents(): Flow<List<CareEvent>> = dao.observeEvents()

    fun observeHealthRecords(): Flow<List<HealthRecord>> = dao.observeHealthRecords()

    fun observeWeights(): Flow<List<WeightEntry>> = dao.observeWeights()

    fun observeHousehold(): Flow<List<HouseholdMember>> = dao.observeHousehold()

    suspend fun savePet(pet: Pet) {
        require(pet.name.isNotBlank()) { "Pet name is required" }
        require(pet.weightKg == null || (pet.weightKg.isFinite() && pet.weightKg > 0)) {
            "Weight must be positive"
        }
        dao.savePet(pet.copy(name = pet.name.trim()))
    }

    suspend fun deletePet(id: String) = dao.deletePet(id)

    suspend fun saveTask(task: CareTask) {
        require(task.title.isNotBlank()) { "Task title is required" }
        require(task.kind in CareKind.all) { "Unknown care type" }
        require(task.recurrenceDays >= 0) { "Recurrence must not be negative" }
        require(task.durationMinutes == null || task.durationMinutes > 0) {
            "Duration must be positive"
        }
        require(task.distanceKm == null || (task.distanceKm.isFinite() && task.distanceKm >= 0)) {
            "Distance must not be negative"
        }
        require(task.startDate == null || task.endDate == null || task.endDate >= task.startDate) {
            "End date precedes start date"
        }
        if (task.kind == CareKind.MEDICATION) {
            require(task.startDate == null || task.time >= task.startDate) {
                "Medication reminder precedes its start date"
            }
            require(task.endDate == null || task.time <= task.endDate) {
                "Medication reminder exceeds its end date"
            }
        }
        dao.saveTask(task.copy(title = task.title.trim()))
    }

    suspend fun deleteTask(id: String) = dao.deleteTask(id)

    suspend fun pauseTask(id: String, paused: Boolean) {
        dao.task(id)?.let { dao.saveTask(it.copy(paused = paused)) }
    }

    suspend fun snoozeTask(id: String, until: Long) {
        require(until > System.currentTimeMillis()) { "Snooze time must be in the future" }
        dao.task(id)?.let { dao.saveTask(it.copy(snoozedUntil = until)) }
    }

    /** Saves the original scheduled instant, so undo restores recurrence without guessing. */
    suspend fun completeTask(taskId: String, at: Long = System.currentTimeMillis()): CareEvent? =
        database.withTransaction {
            val task = dao.task(taskId) ?: return@withTransaction null
            if (task.paused) return@withTransaction null
            val localZone = ZoneId.systemDefault()
            if (
                Instant.ofEpochMilli(task.time).atZone(localZone).toLocalDate() >
                    Instant.ofEpochMilli(at).atZone(localZone).toLocalDate()
            )
                return@withTransaction null
            if (dao.completionCount(task.id, task.time) > 0) return@withTransaction null
            val event =
                CareEvent(
                    taskId = task.id,
                    petId = task.petId,
                    kind = task.kind,
                    title = task.title,
                    completedAt = at,
                    scheduledAt = task.time,
                    notes = task.notes,
                    assignedTo = task.assignedTo,
                    durationMinutes = task.durationMinutes,
                    distanceKm = task.distanceKm,
                )
            dao.saveEvent(event)
            if (task.recurrenceDays > 0) {
                val next =
                    Recurrence.nextAfter(task.time, task.recurrenceDays, maxOf(at, task.time))
                val expired = task.endDate?.let { next > it } ?: false
                dao.saveTask(
                    task.copy(
                        time = if (expired) task.time else next,
                        paused = expired,
                        snoozedUntil = null,
                    )
                )
            } else dao.saveTask(task.copy(paused = true, snoozedUntil = null))
            event
        }

    suspend fun undoCompletion(eventId: String) =
        database.withTransaction {
            val event = dao.event(eventId) ?: return@withTransaction
            event.taskId?.let { id ->
                dao.task(id)?.let { task ->
                    // Do not undo past a newer completion: only the latest completion can rewind a
                    // routine.
                    val expectedNext =
                        if (task.recurrenceDays > 0 && event.scheduledAt != null)
                            Recurrence.nextAfter(
                                event.scheduledAt,
                                task.recurrenceDays,
                                maxOf(event.completedAt, event.scheduledAt),
                            )
                        else event.scheduledAt
                    if (
                        task.time == expectedNext ||
                            task.recurrenceDays == 0 ||
                            (task.paused && task.time == event.scheduledAt)
                    )
                        dao.saveTask(
                            task.copy(time = event.scheduledAt ?: task.time, paused = false)
                        )
                }
            }
            dao.deleteEvent(eventId)
        }

    suspend fun logWalk(
        petId: String,
        minutes: Int,
        distanceKm: Double? = null,
        notes: String = "",
        at: Long = System.currentTimeMillis(),
    ) {
        require(minutes > 0)
        require(distanceKm == null || (distanceKm.isFinite() && distanceKm >= 0))
        require(at <= System.currentTimeMillis() + 5 * 60 * 1000) {
            "Walk completion cannot be in the future"
        }
        dao.saveEvent(
            CareEvent(
                taskId = null,
                petId = petId,
                kind = CareKind.WALKING,
                title = "",
                completedAt = at,
                durationMinutes = minutes,
                distanceKm = distanceKm,
                notes = notes,
            )
        )
    }

    suspend fun saveHealthRecord(record: HealthRecord) {
        require(record.title.isNotBlank())
        dao.saveHealthRecord(record)
    }

    suspend fun deleteHealthRecord(id: String) = dao.deleteHealthRecord(id)

    suspend fun saveWeight(entry: WeightEntry) {
        require(entry.weightKg.isFinite() && entry.weightKg > 0)
        dao.saveWeight(entry)
    }

    suspend fun deleteWeight(id: String) = dao.deleteWeight(id)

    suspend fun saveMember(member: HouseholdMember) {
        require(member.name.isNotBlank())
        dao.saveMember(member)
    }

    suspend fun deleteMember(id: String) = dao.deleteMember(id)

    suspend fun deleteAll() =
        database.withTransaction {
            dao.deletePets()
            dao.deleteMembers()
        }

    suspend fun snapshot(): BackupSnapshot =
        database.withTransaction {
            BackupSnapshot(
                dao.pets(),
                dao.tasks(),
                dao.events(),
                dao.records(),
                dao.weights(),
                dao.members(),
            )
        }

    suspend fun restore(snapshot: BackupSnapshot) =
        database.withTransaction {
            BackupCodec.validate(snapshot)
            val existingPets = dao.pets().associateBy { it.id }
            val existingRecords = dao.records().associateBy { it.id }
            snapshot.pets.forEach {
                dao.savePet(it.copy(photoPath = existingPets[it.id]?.photoPath))
            }
            snapshot.members.forEach { dao.saveMember(it) }
            snapshot.tasks.forEach { dao.saveTask(it) }
            snapshot.events.forEach { dao.saveEvent(it) }
            snapshot.records.forEach {
                dao.saveHealthRecord(
                    it.copy(attachmentPath = existingRecords[it.id]?.attachmentPath)
                )
            }
            snapshot.weights.forEach { dao.saveWeight(it) }
        }
}

object Recurrence {
    /** Calendar days preserve the household's local clock through daylight-saving transitions. */
    fun nextAfter(
        scheduledAt: Long,
        days: Int,
        after: Long,
        zone: ZoneId = ZoneId.systemDefault(),
    ): Long {
        require(days > 0)
        var next = Instant.ofEpochMilli(scheduledAt).atZone(zone)
        val afterTime = Instant.ofEpochMilli(after)
        if (next.toInstant() > afterTime) return scheduledAt
        val elapsedDays =
            java.time.temporal.ChronoUnit.DAYS.between(
                next.toLocalDate(),
                afterTime.atZone(zone).toLocalDate(),
            )
        next = next.plusDays(maxOf(1, elapsedDays / days) * days)
        while (next.toInstant() <= afterTime) next = next.plusDays(days.toLong())
        return next.toInstant().toEpochMilli()
    }
}
