package com.nexus.personaldashboard.ui.screens.home

import com.nexus.personaldashboard.domain.model.NotificationItem

data class HomeUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationItem> = emptyList(),
    val coinsBalance: Int = 500,
    val error: String? = null
)
