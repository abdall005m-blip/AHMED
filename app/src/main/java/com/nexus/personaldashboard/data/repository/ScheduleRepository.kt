package com.nexus.personaldashboard.data.repository

import com.nexus.personaldashboard.data.db.dao.ScheduleDao
import com.nexus.personaldashboard.data.db.entity.toEntity
import com.nexus.personaldashboard.domain.model.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
    private val dao: ScheduleDao
) {
    fun getAllSchedules(): Flow<List<Schedule>> =
        dao.getAllSchedules().map { list -> list.map { it.toDomain() } }

    fun getSchedulesByDay(dayOfWeek: Int): Flow<List<Schedule>> =
        dao.getSchedulesByDay(dayOfWeek).map { list -> list.map { it.toDomain() } }

    fun searchSchedules(query: String): Flow<List<Schedule>> =
        dao.searchSchedules(query).map { list -> list.map { it.toDomain() } }

    suspend fun insert(schedule: Schedule): Long = dao.insert(schedule.toEntity())
    suspend fun update(schedule: Schedule) = dao.update(schedule.toEntity())
    suspend fun delete(schedule: Schedule) = dao.delete(schedule.toEntity())
    suspend fun deleteAll() = dao.deleteAll()
}
