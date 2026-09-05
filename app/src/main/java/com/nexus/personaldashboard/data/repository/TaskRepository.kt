package com.nexus.personaldashboard.data.repository

import com.nexus.personaldashboard.data.db.dao.TaskDao
import com.nexus.personaldashboard.data.db.entity.toEntity
import com.nexus.personaldashboard.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val dao: TaskDao
) {
    fun getAllTasks(): Flow<List<Task>> =
        dao.getAllTasks().map { list -> list.map { it.toDomain() } }

    fun getTodayTasks(): Flow<List<Task>> =
        dao.getTodayTasks().map { list -> list.map { it.toDomain() } }

    fun getUpcomingTasks(): Flow<List<Task>> =
        dao.getUpcomingTasks().map { list -> list.map { it.toDomain() } }

    fun getCompletedTasks(): Flow<List<Task>> =
        dao.getCompletedTasks().map { list -> list.map { it.toDomain() } }

    fun searchTasks(query: String): Flow<List<Task>> =
        dao.searchTasks(query).map { list -> list.map { it.toDomain() } }

    suspend fun insert(task: Task): Long = dao.insert(task.toEntity())
    suspend fun update(task: Task) = dao.update(task.toEntity())
    suspend fun delete(task: Task) = dao.delete(task.toEntity())
    suspend fun setCompleted(id: Long, completed: Boolean) = dao.setCompleted(id, completed)
    suspend fun deleteCompleted() = dao.deleteCompleted()
    suspend fun deleteAll() = dao.deleteAll()
}
