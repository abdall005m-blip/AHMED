package com.nexus.personaldashboard.domain.model

data class NotificationItem(
    val id: Long = 0,
    val appPackage: String,
    val appName: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val priority: NotificationPriority = NotificationPriority.LOW,
    val isRead: Boolean = false,
    val isSilentMode: Boolean = false,
    val isDND: Boolean = false,
    val category: String = ""
)

enum class NotificationPriority { HIGH, MEDIUM, LOW }
