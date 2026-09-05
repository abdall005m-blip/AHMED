package com.nexus.personaldashboard.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.personaldashboard.data.repository.AIAppRepository
import com.nexus.personaldashboard.data.repository.NotificationRepository
import com.nexus.personaldashboard.data.repository.ScheduleRepository
import com.nexus.personaldashboard.data.repository.TaskRepository
import com.nexus.personaldashboard.domain.model.AIApp
import com.nexus.personaldashboard.domain.model.NotificationItem
import com.nexus.personaldashboard.domain.model.Schedule
import com.nexus.personaldashboard.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchResults(
    val notifications: List<NotificationItem> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val schedules: List<Schedule> = emptyList(),
    val aiApps: List<AIApp> = emptyList()
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val notifRepo: NotificationRepository,
    private val taskRepo: TaskRepository,
    private val scheduleRepo: ScheduleRepository,
    private val aiAppRepo: AIAppRepository
) : ViewModel() {

    val query = MutableStateFlow("")

    val results: StateFlow<SearchResults> = query
        .debounce(300)
        .flatMapLatest { q ->
            if (q.trim().length < 2) {
                flowOf(SearchResults())
            } else {
                combine(
                    notifRepo.searchNotifications(q.trim()),
                    taskRepo.searchTasks(q.trim()),
                    scheduleRepo.searchSchedules(q.trim()),
                    aiAppRepo.searchAIApps(q.trim())
                ) { notifs, tasks, schedules, apps ->
                    SearchResults(
                        notifications = notifs.take(5),
                        tasks = tasks.take(5),
                        schedules = schedules.take(5),
                        aiApps = apps.take(5)
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchResults())

    fun setQuery(q: String) {
        query.value = q
    }
}
