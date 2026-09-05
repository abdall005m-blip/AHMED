package com.nexus.personaldashboard.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexus.personaldashboard.domain.model.RepeatType
import com.nexus.personaldashboard.domain.model.Schedule

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dayOfWeek: Int,
    val startTime: Long,
    val endTime: Long,
    val repeatType: String = RepeatType.NONE.name,
    val note: String = "",
    val iconName: String = ""
) {
    fun toDomain() = Schedule(
        id = id,
        title = title,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime,
        repeatType = try { RepeatType.valueOf(repeatType) } catch (e: Exception) { RepeatType.NONE },
        note = note,
        iconName = iconName
    )
}

fun Schedule.toEntity() = ScheduleEntity(
    id = id,
    title = title,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime,
    repeatType = repeatType.name,
    note = note,
    iconName = iconName
)
