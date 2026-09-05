package com.nexus.personaldashboard.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.personaldashboard.ui.components.GlassCard
import com.nexus.personaldashboard.ui.navigation.NavRoute
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (NavRoute) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val greeting = getGreeting()
    val dateStr = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { onNavigate(NavRoute.SEARCH) }) {
                        Icon(Icons.Rounded.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { onNavigate(NavRoute.SETTINGS) }) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Today's Tasks
            item {
                SectionCard(
                    title = "Today's Tasks 📝",
                    count = state.todayTasks.size,
                    onClick = { onNavigate(NavRoute.TASKS) }
                ) {
                    if (state.todayTasks.isEmpty()) {
                        Text(
                            "No pending tasks for today 🎉",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        state.todayTasks.forEach { task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Important Notifications
            item {
                SectionCard(
                    title = "Important Notifications 🔔",
                    count = state.importantNotifications.size,
                    onClick = { onNavigate(NavRoute.NOTIFICATIONS) }
                ) {
                    if (state.importantNotifications.isEmpty()) {
                        Text(
                            "No high priority notifications right now",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        state.importantNotifications.forEach { notif ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = notif.appName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = notif.title.ifBlank { notif.content },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Next Schedule
            item {
                SectionCard(
                    title = "Schedule 📅",
                    count = state.upcomingSchedules.size,
                    onClick = { onNavigate(NavRoute.SCHEDULE) }
                ) {
                    if (state.upcomingSchedules.isEmpty()) {
                        Text(
                            "No schedules scheduled for today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        state.upcomingSchedules.forEach { schedule ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = schedule.iconName.ifBlank { "📌" },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = schedule.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // AI Apps quick access
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "AI Hub 🤖",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = { onNavigate(NavRoute.AI_HUB) }) {
                            Text("See all")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (state.aiApps.isEmpty()) {
                        Text(
                            "Add AI apps to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(state.aiApps) { app ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(64.dp)
                                ) {
                                    Text(app.iconEmoji, fontSize = 32.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = app.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    count: Int,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (count > 0) {
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text(count.toString(), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        content()
    }
}

private fun getGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "صباح الخير 👋 Good Morning"
        in 12..16 -> "مساء الخير 👋 Good Afternoon"
        in 17..21 -> "مساء النور 👋 Good Evening"
        else -> "طاب مساؤك 🌙 Good Night"
    }
}
