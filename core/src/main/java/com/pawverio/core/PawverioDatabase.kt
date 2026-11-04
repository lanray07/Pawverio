package com.pawverio.core

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CareDao {
    @Query("SELECT * FROM pets ORDER BY createdAt") fun observePets(): Flow<List<Pet>>

    @Query("SELECT * FROM pets ORDER BY createdAt") suspend fun pets(): List<Pet>

    @Query("SELECT * FROM care_tasks ORDER BY time") suspend fun tasks(): List<CareTask>

    @Query("SELECT * FROM care_events ORDER BY completedAt DESC")
    suspend fun events(): List<CareEvent>

    @Query("SELECT * FROM health_records ORDER BY date DESC")
    suspend fun records(): List<HealthRecord>

    @Query("SELECT * FROM weight_entries ORDER BY date") suspend fun weights(): List<WeightEntry>

    @Query("SELECT * FROM household_members ORDER BY name")
    suspend fun members(): List<HouseholdMember>

    @Query("SELECT * FROM care_tasks ORDER BY time") fun observeTasks(): Flow<List<CareTask>>

    @Query("SELECT * FROM care_tasks WHERE id = :id") suspend fun task(id: String): CareTask?

    @Query("SELECT * FROM care_events ORDER BY completedAt DESC")
    fun observeEvents(): Flow<List<CareEvent>>

    @Query("SELECT * FROM care_events WHERE id = :id") suspend fun event(id: String): CareEvent?

    @Query("SELECT * FROM health_records ORDER BY date DESC")
    fun observeHealthRecords(): Flow<List<HealthRecord>>

    @Query("SELECT * FROM weight_entries ORDER BY date")
    fun observeWeights(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM household_members ORDER BY name")
    fun observeHousehold(): Flow<List<HouseholdMember>>

    @Upsert suspend fun savePet(value: Pet)

    @Upsert suspend fun saveTask(value: CareTask)

    @Upsert suspend fun saveEvent(value: CareEvent)

    @Upsert suspend fun saveHealthRecord(value: HealthRecord)

    @Upsert suspend fun saveWeight(value: WeightEntry)

    @Upsert suspend fun saveMember(value: HouseholdMember)

    @Query("DELETE FROM pets WHERE id = :id") suspend fun deletePet(id: String)

    @Query("DELETE FROM care_tasks WHERE id = :id") suspend fun deleteTask(id: String)

    @Query("DELETE FROM care_events WHERE id = :id") suspend fun deleteEvent(id: String)

    @Query("DELETE FROM health_records WHERE id = :id") suspend fun deleteHealthRecord(id: String)

    @Query("DELETE FROM weight_entries WHERE id = :id") suspend fun deleteWeight(id: String)

    @Query("DELETE FROM household_members WHERE id = :id") suspend fun deleteMember(id: String)

    @Query("DELETE FROM pets") suspend fun deletePets()

    @Query("DELETE FROM household_members") suspend fun deleteMembers()

    @Query("SELECT COUNT(*) FROM care_events WHERE taskId = :taskId AND scheduledAt = :scheduledAt")
    suspend fun completionCount(taskId: String, scheduledAt: Long): Int
}

@Database(
    entities =
        [
            Pet::class,
            CareTask::class,
            CareEvent::class,
            HealthRecord::class,
            WeightEntry::class,
            HouseholdMember::class,
        ],
    version = 1,
    exportSchema = true,
)
abstract class PawverioDatabase : RoomDatabase() {
    abstract fun careDao(): CareDao

    companion object {
        @Volatile private var instance: PawverioDatabase? = null

        fun open(context: Context): PawverioDatabase =
            instance
                ?: synchronized(this) {
                    instance
                        ?: Room.databaseBuilder(
                                context.applicationContext,
                                PawverioDatabase::class.java,
                                "pawverio.db",
                            )
                            .build()
                            .also { instance = it }
                }
    }
}
