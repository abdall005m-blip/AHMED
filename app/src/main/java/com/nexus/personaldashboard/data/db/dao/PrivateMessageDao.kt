package com.nexus.personaldashboard.data.db.dao

import androidx.room.*
import com.nexus.personaldashboard.data.db.entity.PrivateMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrivateMessageDao {
    @Query("SELECT * FROM private_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<PrivateMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: PrivateMessageEntity): Long

    @Query("UPDATE private_messages SET isRead = 1 WHERE senderId != :currentUserId")
    suspend fun markAllAsRead(currentUserId: String)

    @Query("DELETE FROM private_messages")
    suspend fun clearChat()
}
