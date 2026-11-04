package com.pawverio.core

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

object CareKind {
    const val FEEDING = "feeding"
    const val WALKING = "walking"
    const val GROOMING = "grooming"
    const val MEDICATION = "medication"
    const val VET = "vet"
    const val VACCINATION = "vaccination"
    const val CUSTOM = "custom"
    val all = listOf(FEEDING, WALKING, GROOMING, MEDICATION, VET, VACCINATION, CUSTOM)
}

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val species: String = "dog",
    val breed: String = "",
    val birthDate: Long? = null,
    val approximateAge: String = "",
    val weightKg: Double? = null,
    val microchip: String = "",
    val allergies: String = "",
    val notes: String = "",
    val photoPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "care_tasks",
    foreignKeys =
        [
            ForeignKey(
                entity = Pet::class,
                parentColumns = ["id"],
                childColumns = ["petId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("petId"), Index("time")],
)
data class CareTask(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val petId: String,
    val kind: String = CareKind.FEEDING,
    val title: String,
    val time: Long = System.currentTimeMillis(),
    val recurrenceDays: Int = 0,
    val notes: String = "",
    val assignedTo: String = "",
    val paused: Boolean = false,
    val durationMinutes: Int? = null,
    val distanceKm: Double? = null,
    val dosage: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val portion: String = "",
    val mealType: String = "",
    val remindersEnabled: Boolean = true,
    val snoozedUntil: Long? = null,
)

@Entity(
    tableName = "care_events",
    foreignKeys =
        [
            ForeignKey(
                entity = Pet::class,
                parentColumns = ["id"],
                childColumns = ["petId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("petId"), Index("taskId")],
)
data class CareEvent(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val taskId: String?,
    val petId: String,
    val kind: String,
    val title: String,
    val completedAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null,
    val notes: String = "",
    val assignedTo: String = "",
    val durationMinutes: Int? = null,
    val distanceKm: Double? = null,
)

@Entity(
    tableName = "health_records",
    foreignKeys =
        [
            ForeignKey(
                entity = Pet::class,
                parentColumns = ["id"],
                childColumns = ["petId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("petId")],
)
data class HealthRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val petId: String,
    val kind: String = CareKind.VET,
    val title: String,
    val date: Long = System.currentTimeMillis(),
    val nextDueDate: Long? = null,
    val provider: String = "",
    val contact: String = "",
    val notes: String = "",
    val attachmentPath: String? = null,
)

@Entity(
    tableName = "weight_entries",
    foreignKeys =
        [
            ForeignKey(
                entity = Pet::class,
                parentColumns = ["id"],
                childColumns = ["petId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices = [Index("petId"), Index("date")],
)
data class WeightEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val petId: String,
    val weightKg: Double,
    val date: Long = System.currentTimeMillis(),
    val measurements: String = "",
    val notes: String = "",
)

@Entity(tableName = "household_members")
data class HouseholdMember(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val contact: String = "",
    val notes: String = "",
)
