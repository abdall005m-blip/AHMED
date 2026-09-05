package com.nexus.personaldashboard.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.personaldashboard.data.preferences.UserPreferencesRepository
import com.nexus.personaldashboard.data.repository.AIAppRepository
import com.nexus.personaldashboard.data.repository.NotificationRepository
import com.nexus.personaldashboard.data.repository.ScheduleRepository
import com.nexus.personaldashboard.data.repository.TaskRepository
import com.nexus.personaldashboard.domain.model.AIApp
import com.nexus.personaldashboard.domain.model.NotificationItem
import com.nexus.personaldashboard.domain.model.Schedule
import com.nexus.personaldashboard.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val todayTasks: List<Task> = emptyList(),
    val importantNotifications: List<NotificationItem> = emptyList(),
    val upcomingSchedules: List<Schedule> = emptyList(),
    val aiApps: List<AIApp> = emptyList(),
    val userName: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    taskRepo: TaskRepository,
    notifRepo: NotificationRepository,
    scheduleRepo: ScheduleRepository,
    aiAppRepo: AIAppRepository,
    prefsRepo: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        taskRepo.getTodayTasks(),
        notifRepo.getImportantNotifications(),
        scheduleRepo.getAllSchedules(),
        aiAppRepo.getAllAIApps(),
        prefsRepo.userName
    ) { todayTasks, importantNotifs, schedules, aiApps, userName ->
        HomeUiState(
            todayTasks = todayTasks.take(5),
            importantNotifications = importantNotifs.take(3),
            upcomingSchedules = schedules.take(3),
            aiApps = aiApps.take(6),
            userName = userName
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
