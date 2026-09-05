package com.nexus.personaldashboard.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexus.personaldashboard.domain.model.RepeatType
import com.nexus.personaldashboard.domain.model.Task
import com.nexus.personaldashboard.domain.model.TaskPriority

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val dueTime: Long? = null,
    val priority: String = TaskPriority.MEDIUM.name,
    val isCompleted: Boolean = false,
    val repeatType: String = RepeatType.NONE.name,
    val reminderEnabled: Boolean = false
) {
    fun toDomain() = Task(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate,
        dueTime = dueTime,
        priority = try { TaskPriority.valueOf(priority) } catch (e: Exception) { TaskPriority.MEDIUM },
        isCompleted = isCompleted,
        repeatType = try { RepeatType.valueOf(repeatType) } catch (e: Exception) { RepeatType.NONE },
        reminderEnabled = reminderEnabled
    )
}

fun Task.toEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    dueDate = dueDate,
    dueTime = dueTime,
    priority = priority.name,
    isCompleted = isCompleted,
    repeatType = repeatType.name,
    reminderEnabled = reminderEnabled
)
