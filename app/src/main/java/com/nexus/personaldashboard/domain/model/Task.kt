package com.nexus.personaldashboard.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val dueTime: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val repeatType: RepeatType = RepeatType.NONE,
    val reminderEnabled: Boolean = false
)

enum class TaskPriority { HIGH, MEDIUM, LOW }
enum class RepeatType { NONE, DAILY, WEEKLY, MONTHLY, CUSTOM }
