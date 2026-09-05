package com.nexus.personaldashboard.domain.model

data class Schedule(
    val id: Long = 0,
    val title: String,
    val dayOfWeek: Int, // 1=Mon..7=Sun
    val startTime: Long,
    val endTime: Long,
    val repeatType: RepeatType = RepeatType.NONE,
    val note: String = "",
    val iconName: String = ""
)
