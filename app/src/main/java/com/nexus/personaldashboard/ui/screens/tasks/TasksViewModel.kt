package com.nexus.personaldashboard.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.personaldashboard.data.repository.TaskRepository
import com.nexus.personaldashboard.domain.model.Task
import com.nexus.personaldashboard.util.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repo: TaskRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val todayTasks = repo.getTodayTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingTasks = repo.getUpcomingTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTasks = repo.getCompletedTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleComplete(task: Task) = viewModelScope.launch {
        val newStatus = !task.isCompleted
        repo.setCompleted(task.id, newStatus)
        if (newStatus) {
            reminderScheduler.cancelTaskReminder(task.id)
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        reminderScheduler.cancelTaskReminder(task.id)
        repo.delete(task)
    }

    fun upsertTask(task: Task) = viewModelScope.launch {
        val id = if (task.id == 0L) {
            repo.insert(task)
        } else {
            repo.update(task)
            task.id
        }
        if (task.reminderEnabled && !task.isCompleted) {
            reminderScheduler.scheduleTaskReminder(task.copy(id = id))
        } else {
            reminderScheduler.cancelTaskReminder(id)
        }
    }
}
