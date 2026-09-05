package com.nexus.personaldashboard.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.personaldashboard.data.preferences.UserPreferencesRepository
import com.nexus.personaldashboard.data.repository.NotificationRepository
import com.nexus.personaldashboard.data.repository.ScheduleRepository
import com.nexus.personaldashboard.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val prefsRepo: UserPreferencesRepository,
    private val notifRepo: NotificationRepository,
    private val taskRepo: TaskRepository,
    private val scheduleRepo: ScheduleRepository
) : ViewModel() {

    val themeMode = prefsRepo.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val accentColor = prefsRepo.accentColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "#7C3AED")

    fun setTheme(mode: String) = viewModelScope.launch {
        prefsRepo.setThemeMode(mode)
    }

    fun setAccentColor(color: String) = viewModelScope.launch {
        prefsRepo.setAccentColor(color)
    }

    fun clearNotifications() = viewModelScope.launch {
        notifRepo.deleteAll()
    }

    fun clearTasks() = viewModelScope.launch {
        taskRepo.deleteAll()
    }

    fun clearSchedules() = viewModelScope.launch {
        scheduleRepo.deleteAll()
    }
}
