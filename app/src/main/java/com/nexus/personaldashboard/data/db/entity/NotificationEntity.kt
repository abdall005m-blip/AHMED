package com.nexus.personaldashboard.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexus.personaldashboard.domain.model.NotificationItem
import com.nexus.personaldashboard.domain.model.NotificationPriority

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appPackage: String,
    val appName: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val priority: String = NotificationPriority.LOW.name,
    val isRead: Boolean = false,
    val isSilentMode: Boolean = false,
    val isDND: Boolean = false,
    val category: String = ""
) {
    fun toDomain() = NotificationItem(
        id = id,
        appPackage = appPackage,
        appName = appName,
        title = title,
        content = content,
        timestamp = timestamp,
        priority = try { NotificationPriority.valueOf(priority) } catch (e: Exception) { NotificationPriority.LOW },
        isRead = isRead,
        isSilentMode = isSilentMode,
        isDND = isDND,
        category = category
    )
}

fun NotificationItem.toEntity() = NotificationEntity(
    id = id,
    appPackage = appPackage,
    appName = appName,
    title = title,
    content = content,
    timestamp = timestamp,
    priority = priority.name,
    isRead = isRead,
    isSilentMode = isSilentMode,
    isDND = isDND,
    category = category
)
