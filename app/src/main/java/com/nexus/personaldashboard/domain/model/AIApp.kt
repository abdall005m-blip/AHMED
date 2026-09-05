package com.nexus.personaldashboard.domain.model

data class AIApp(
    val id: Long = 0,
    val name: String,
    val packageName: String,
    val deepLink: String = "",
    val websiteUrl: String,
    val description: String = "",
    val iconEmoji: String = "🤖",
    val isPinned: Boolean = false,
    val orderIndex: Int = 0
)
