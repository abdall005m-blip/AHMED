package com.nexus.personaldashboard.ui.screens.entertainment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.personaldashboard.data.repository.EntertainmentRepository
import com.nexus.personaldashboard.domain.model.PrivateMessage
import com.nexus.personaldashboard.domain.model.PrivateUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EntertainmentTab {
    CHAT, GAMES, ISLAMIC, MOOD
}

data class EntertainmentUiState(
    val currentTab: EntertainmentTab = EntertainmentTab.CHAT,
    val currentUserId: String? = null, // "ahmed" or "rody"
    val isLoginDialogVisible: Boolean = false,
    val loginError: String? = null,
    val chatDurationDays: Long = 0,
    val ahmedUser: PrivateUser? = null,
    val rodyUser: PrivateUser? = null,
    val messages: List<PrivateMessage> = emptyList(),
    // XO Game
    val xoBoard: List<String?> = List(9) { null },
    val xoTurn: String = "ahmed",
    val xoWinner: String? = null,
    // Racing Game
    val ahmedCarPos: Int = 0,
    val rodyCarPos: Int = 0,
    val racingWinner: String? = null,
    // Connect 4
    val connect4Board: List<List<String?>> = List(6) { List(7) { null } },
    val connect4Turn: String = "ahmed",
    val connect4Winner: String? = null,
    // Quran & Azkar
    val selectedAzkarTab: String = "morning",
    val selectedSurahId: Int? = null
)

@HiltViewModel
class EntertainmentViewModel @Inject constructor(
    private val repository: EntertainmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntertainmentUiState())
    val uiState: StateFlow<EntertainmentUiState> = _uiState.asStateFlow()

    init {
        // Observe users & messages
        viewModelScope.launch {
            repository.ahmedUser.collect { user ->
                _uiState.update { it.copy(ahmedUser = user) }
            }
        }
        viewModelScope.launch {
            repository.rodyUser.collect { user ->
                _uiState.update { it.copy(rodyUser = user) }
            }
        }
        viewModelScope.launch {
            repository.getAllMessages().collect { msgs ->
                _uiState.update { it.copy(messages = msgs) }
            }
        }

        // Calculate complete days
        val days = (System.currentTimeMillis() - repository.chatCreatedAt) / (1000L * 60 * 60 * 24)
        _uiState.update { it.copy(chatDurationDays = days) }

        // Insert initial seed messages if empty
        viewModelScope.launch {
            val existing = repository.getAllMessages().first()
            if (existing.isEmpty()) {
                repository.sendMessage(
                    PrivateMessage(
                        senderId = "ahmed",
                        content = "أهلاً يا رودي! نورتي التطبيق والشات الخاص 🌸",
                        decoration = "🌸"
                    )
                )
                repository.sendMessage(
                    PrivateMessage(
                        senderId = "rody",
                        content = "الحمد لله، التطبيق بقى جميل جداً وفيه الألعاب والأذكار مع بعض! ❤️",
                        decoration = "❤️"
                    )
                )
            }
        }
    }

    fun selectTab(tab: EntertainmentTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun login(userId: String, code: String) {
        if (repository.verifyCode(userId, code)) {
            _uiState.update {
                it.copy(
                    currentUserId = userId,
                    isLoginDialogVisible = false,
                    loginError = null
                )
            }
        } else {
            _uiState.update { it.copy(loginError = "رمز الدخول غير صحيح! حاول مرة أخرى") }
        }
    }

    fun logout() {
        _uiState.update { it.copy(currentUserId = null) }
    }

    fun sendMessage(content: String, decoration: String = "") {
        val user = _uiState.value.currentUserId ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(
                PrivateMessage(
                    senderId = user,
                    content = content.trim(),
                    decoration = decoration
                )
            )
        }
    }

    fun setBubbleStyle(style: String) {
        val user = _uiState.value.currentUserId ?: return
        repository.setBubbleStyle(user, style)
    }

    fun updateMood(emoji: String, text: String) {
        val user = _uiState.value.currentUserId ?: return
        repository.updateMood(user, emoji, text)
    }

    fun updateNote(note: String) {
        val user = _uiState.value.currentUserId ?: return
        repository.updateNote(user, note)
    }

    // --- XO GAME ---
    fun makeXOMove(index: Int) {
        val state = _uiState.value
        val user = state.currentUserId ?: return
        if (state.xoBoard[index] != null || state.xoWinner != null || state.xoTurn != user) return

        val symbol = if (user == "ahmed") "X" else "O"
        val newBoard = state.xoBoard.toMutableList().also { it[index] = symbol }

        val lines = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )
        var winner: String? = null
        for (line in lines) {
            if (newBoard[line[0]] != null && newBoard[line[0]] == newBoard[line[1]] && newBoard[line[0]] == newBoard[line[2]]) {
                winner = user
                repository.adjustCoins(user, 20)
                break
            }
        }
        if (winner == null && newBoard.all { it != null }) {
            winner = "draw"
        }

        _uiState.update {
            it.copy(
                xoBoard = newBoard,
                xoWinner = winner,
                xoTurn = if (winner != null) it.xoTurn else (if (user == "ahmed") "rody" else "ahmed")
            )
        }
    }

    fun resetXO() {
        _uiState.update {
            it.copy(
                xoBoard = List(9) { null },
                xoWinner = null,
                xoTurn = "ahmed"
            )
        }
    }

    // --- RACING GAME ---
    fun tapCar() {
        val state = _uiState.value
        val user = state.currentUserId ?: return
        if (state.racingWinner != null) return

        if (user == "ahmed") {
            val newPos = state.ahmedCarPos + 5
            val winner = if (newPos >= 100) { repository.adjustCoins("ahmed", 30); "ahmed" } else null
            _uiState.update { it.copy(ahmedCarPos = newPos, racingWinner = winner) }
        } else {
            val newPos = state.rodyCarPos + 5
            val winner = if (newPos >= 100) { repository.adjustCoins("rody", 30); "rody" } else null
            _uiState.update { it.copy(rodyCarPos = newPos, racingWinner = winner) }
        }
    }

    fun resetRacing() {
        _uiState.update {
            it.copy(
                ahmedCarPos = 0,
                rodyCarPos = 0,
                racingWinner = null
            )
        }
    }

    // --- AZKAR & QURAN ---
    fun setAzkarTab(tab: String) {
        _uiState.update { it.copy(selectedAzkarTab = tab) }
    }
}
