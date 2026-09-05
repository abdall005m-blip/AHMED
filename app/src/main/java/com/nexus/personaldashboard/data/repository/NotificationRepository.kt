package com.nexus.personaldashboard.data.repository

import com.nexus.personaldashboard.data.db.dao.NotificationDao
import com.nexus.personaldashboard.data.db.entity.toEntity
import com.nexus.personaldashboard.domain.model.NotificationItem
import com.nexus.personaldashboard.domain.model.NotificationPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val dao: NotificationDao
) {
    fun getAllNotifications(): Flow<List<NotificationItem>> =
        dao.getAllNotifications().map { list -> list.map { it.toDomain() } }

    fun getImportantNotifications(): Flow<List<NotificationItem>> =
        dao.getImportantNotifications().map { list -> list.map { it.toDomain() } }

    fun getSilentDndNotifications(): Flow<List<NotificationItem>> =
        dao.getSilentDndNotifications().map { list -> list.map { it.toDomain() } }

    fun searchNotifications(query: String): Flow<List<NotificationItem>> =
        dao.searchNotifications(query).map { list -> list.map { it.toDomain() } }

    suspend fun insert(notification: NotificationItem): Long = dao.insert(notification.toEntity())
    suspend fun markAsRead(id: Long) = dao.markAsRead(id)
    suspend fun updatePriority(id: Long, priority: NotificationPriority) =
        dao.updatePriority(id, priority.name)
    suspend fun delete(notification: NotificationItem) = dao.delete(notification.toEntity())
    suspend fun deleteAll() = dao.deleteAll()
}
