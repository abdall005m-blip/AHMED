package com.nexus.personaldashboard.ui.screens.settings

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.personaldashboard.service.NotificationAccessHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    var isNotifEnabled by remember {
        mutableStateOf(NotificationAccessHelper.isNotificationAccessEnabled(context))
    }
    var showClearDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isNotifEnabled = NotificationAccessHelper.isNotificationAccessEnabled(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings ⚙️", fontWeight = FontWeight.Bold) },
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
                SettingsSection(title = "Notification Access") {
                    SettingsItem(
                        icon = Icons.Rounded.Notifications,
                        title = "Notification Access",
                        subtitle = if (isNotifEnabled) "● Enabled" else "○ Disabled — Tap to open Settings",
                        onClick = {
                            NotificationAccessHelper.openNotificationAccessSettings(context)
                        }
                    )
                }
            }

            item {
                SettingsSection(title = "Appearance") {
                    Text(
                        text = "Theme Mode",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        listOf("LIGHT", "DARK", "SYSTEM").forEach { mode ->
                            FilterChip(
                                selected = themeMode == mode,
                                onClick = { viewModel.setTheme(mode) },
                                label = { Text(mode.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Privacy & Local Data") {
                    SettingsItem(
                        icon = Icons.Rounded.NotificationsNone,
                        title = "Clear Notification History",
                        subtitle = "Delete all stored notifications from database",
                        onClick = { showClearDialog = "notifications" },
                        isDestructive = true
                    )
                    SettingsItem(
                        icon = Icons.Rounded.CheckCircleOutline,
                        title = "Clear All Tasks",
                        subtitle = "Delete all tasks from local database",
                        onClick = { showClearDialog = "tasks" },
                        isDestructive = true
                    )
                    SettingsItem(
                        icon = Icons.Rounded.CalendarMonth,
                        title = "Clear All Schedules",
                        subtitle = "Delete all schedules from local database",
                        onClick = { showClearDialog = "schedules" },
                        isDestructive = true
                    )
                }
            }
        }
    }

    if (showClearDialog != null) {
        AlertDialog(
            onDismissRequest = { showClearDialog = null },
            title = { Text("Confirm Clear") },
            text = { Text("Are you sure you want to delete all ${showClearDialog}? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    when (showClearDialog) {
                        "notifications" -> viewModel.clearNotifications()
                        "tasks" -> viewModel.clearTasks()
                        "schedules" -> viewModel.clearSchedules()
                    }
                    showClearDialog = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
        ) {
            Column(modifier = Modifier.padding(8.dp), content = content)
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
    )
    HorizontalDivider(
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )
}
