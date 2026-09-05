package com.nexus.personaldashboard.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexus.personaldashboard.domain.model.MessageType
import com.nexus.personaldashboard.domain.model.PrivateMessage

@Entity(tableName = "private_messages")
data class PrivateMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: String,
    val content: String,
    val type: String = MessageType.TEXT.name,
    val decoration: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
) {
    fun toDomain() = PrivateMessage(
        id = id,
        senderId = senderId,
        content = content,
        type = MessageType.valueOf(type),
        decoration = decoration,
        timestamp = timestamp,
        isRead = isRead
    )
}

fun PrivateMessage.toEntity() = PrivateMessageEntity(
    id = id,
    senderId = senderId,
    content = content,
    type = type.name,
    decoration = decoration,
    timestamp = timestamp,
    isRead = isRead
)
