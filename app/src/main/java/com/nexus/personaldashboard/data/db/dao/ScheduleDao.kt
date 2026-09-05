package com.nexus.personaldashboard.data.db.dao

import androidx.room.*
import com.nexus.personaldashboard.data.db.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getSchedulesByDay(dayOfWeek: Int): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE title LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%' ORDER BY dayOfWeek ASC")
    fun searchSchedules(query: String): Flow<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity): Long

    @Update
    suspend fun update(schedule: ScheduleEntity)

    @Delete
    suspend fun delete(schedule: ScheduleEntity)

    @Query("DELETE FROM schedules")
    suspend fun deleteAll()
}
