package com.nexus.personaldashboard.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.personaldashboard.data.repository.NotificationRepository
import com.nexus.personaldashboard.domain.model.NotificationItem
import com.nexus.personaldashboard.domain.model.NotificationPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repo: NotificationRepository
) : ViewModel() {

    val allNotifications: StateFlow<List<NotificationItem>> = repo.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val importantNotifications: StateFlow<List<NotificationItem>> = repo.getImportantNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val silentDndNotifications: StateFlow<List<NotificationItem>> = repo.getSilentDndNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markAsRead(id: Long) = viewModelScope.launch {
        repo.markAsRead(id)
    }

    fun delete(item: NotificationItem) = viewModelScope.launch {
        repo.delete(item)
    }

    fun updatePriority(id: Long, priority: NotificationPriority) = viewModelScope.launch {
        repo.updatePriority(id, priority)
    }

    fun deleteAll() = viewModelScope.launch {
        repo.deleteAll()
    }
}
