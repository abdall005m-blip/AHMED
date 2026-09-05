package com.nexus.personaldashboard.data.repository

import com.nexus.personaldashboard.data.db.dao.PrivateMessageDao
import com.nexus.personaldashboard.data.db.entity.toEntity
import com.nexus.personaldashboard.domain.model.PrivateMessage
import com.nexus.personaldashboard.domain.model.PrivateUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntertainmentRepository @Inject constructor(
    private val messageDao: PrivateMessageDao
) {
    // Initial creation timestamp: 18 days ago (never resets)
    val chatCreatedAt: Long = System.currentTimeMillis() - (18L * 24 * 60 * 60 * 1000 + 4L * 3600 * 1000)

    private val _ahmedUser = MutableStateFlow(
        PrivateUser(
            id = "ahmed",
            name = "Ahmed",
            codeHash = hash("AHM4821"),
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80",
            note = "يومي كان حلو النهارده 🌸",
            moodEmoji = "😊 مبسوط",
            moodText = "الحمد لله كل حاجة تمام",
            bubbleStyle = "flowers",
            coins = 150
        )
    )
    val ahmedUser: StateFlow<PrivateUser> = _ahmedUser.asStateFlow()

    private val _rodyUser = MutableStateFlow(
        PrivateUser(
            id = "rody",
            name = "Rody",
            codeHash = hash("ROD7354"),
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80",
            note = "محتاجة أروق شوية ☕",
            moodEmoji = "✨ كويسة",
            moodText = "بشرب قهوتي المفضلة",
            bubbleStyle = "hearts",
            coins = 180
        )
    )
    val rodyUser: StateFlow<PrivateUser> = _rodyUser.asStateFlow()

    fun getAllMessages(): Flow<List<PrivateMessage>> =
        messageDao.getAllMessages().map { entities -> entities.map { it.toDomain() } }

    suspend fun sendMessage(message: PrivateMessage): Long =
        messageDao.insertMessage(message.toEntity())

    suspend fun markAllAsRead(currentUserId: String) =
        messageDao.markAllAsRead(currentUserId)

    fun verifyCode(userId: String, code: String): Boolean {
        val targetUser = if (userId == "ahmed") _ahmedUser.value else _rodyUser.value
        return targetUser.codeHash == hash(code.trim())
    }

    fun updateMood(userId: String, emoji: String, text: String) {
        if (userId == "ahmed") {
            _ahmedUser.value = _ahmedUser.value.copy(moodEmoji = emoji, moodText = text)
        } else {
            _rodyUser.value = _rodyUser.value.copy(moodEmoji = emoji, moodText = text)
        }
    }

    fun updateNote(userId: String, note: String) {
        if (userId == "ahmed") {
            _ahmedUser.value = _ahmedUser.value.copy(note = note)
        } else {
            _rodyUser.value = _rodyUser.value.copy(note = note)
        }
    }

    fun setBubbleStyle(userId: String, style: String) {
        if (userId == "ahmed") {
            _ahmedUser.value = _ahmedUser.value.copy(bubbleStyle = style)
        } else {
            _rodyUser.value = _rodyUser.value.copy(bubbleStyle = style)
        }
    }

    fun adjustCoins(userId: String, delta: Int) {
        if (userId == "ahmed") {
            _ahmedUser.value = _ahmedUser.value.copy(coins = maxOf(0, _ahmedUser.value.coins + delta))
        } else {
            _rodyUser.value = _rodyUser.value.copy(coins = maxOf(0, _rodyUser.value.coins + delta))
        }
    }

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
