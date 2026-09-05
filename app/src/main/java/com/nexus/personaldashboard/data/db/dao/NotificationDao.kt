package com.nexus.personaldashboard.data.db.dao

import androidx.room.*
import com.nexus.personaldashboard.data.db.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE priority = 'HIGH' ORDER BY timestamp DESC")
    fun getImportantNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE isSilentMode = 1 OR isDND = 1 ORDER BY timestamp DESC")
    fun getSilentDndNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR appName LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchNotifications(query: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Update
    suspend fun update(notification: NotificationEntity)

    @Delete
    suspend fun delete(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET priority = :priority WHERE id = :id")
    suspend fun updatePriority(id: Long, priority: String)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()
}
