package com.nexus.personaldashboard.domain.model

data class PrivateUser(
    val id: String, // "ahmed" or "rody"
    val name: String,
    val codeHash: String,
    val avatarUrl: String,
    val note: String = "",
    val moodEmoji: String = "😊",
    val moodText: String = "",
    val bubbleStyle: String = "flowers",
    val coins: Int = 150,
    val isOnline: Boolean = true,
    val lastSeen: Long = System.currentTimeMillis()
)

data class PrivateMessage(
    val id: Long = 0,
    val senderId: String, // "ahmed" or "rody"
    val content: String,
    val type: MessageType = MessageType.TEXT,
    val decoration: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class MessageType {
    TEXT, IMAGE, VOICE
}

data class QuizQuestion(
    val id: Int,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val points: Int = 15
)

data class AzkarItem(
    val id: String,
    val text: String,
    val count: Int,
    val virtue: String,
    val isMorning: Boolean
)

data class SurahInfo(
    val id: Int,
    val name: String,
    val englishName: String,
    val versesCount: Int,
    val type: String
)
