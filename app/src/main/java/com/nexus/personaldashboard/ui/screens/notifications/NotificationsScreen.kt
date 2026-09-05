package com.nexus.personaldashboard.ui.screens.notifications

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.personaldashboard.domain.model.NotificationItem
import com.nexus.personaldashboard.service.NotificationAccessHelper
import com.nexus.personaldashboard.ui.components.EmptyState
import com.nexus.personaldashboard.ui.components.GlassCard
import com.nexus.personaldashboard.ui.components.NotifPriorityBadge
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val importantNotifications by viewModel.importantNotifications.collectAsStateWithLifecycle()
    val silentDndNotifications by viewModel.silentDndNotifications.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var isAccessEnabled by remember {
        mutableStateOf(NotificationAccessHelper.isNotificationAccessEnabled(context))
    }

    LaunchedEffect(Unit) {
        isAccessEnabled = NotificationAccessHelper.isNotificationAccessEnabled(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications 🔔", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!isAccessEnabled) {
                NotificationAccessBanner(context)
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All (${allNotifications.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Important (${importantNotifications.size})") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Silent & DND (${silentDndNotifications.size})") }
                )
            }

            val currentList = when (selectedTab) {
                1 -> importantNotifications
                2 -> silentDndNotifications
                else -> allNotifications
            }

            if (currentList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        emoji = if (!isAccessEnabled) "🔐" else "🔔",
                        title = if (!isAccessEnabled) "Notification Access Needed" else "No notifications here",
                        subtitle = if (!isAccessEnabled) "Enable Notification Access in Settings to start gathering notifications" else "New notifications will appear here automatically"
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currentList, key = { it.id }) { item ->
                        NotificationCard(
                            item = item,
                            onMarkRead = { viewModel.markAsRead(item.id) },
                            onDelete = { viewModel.delete(item) },
                            onOpen = { openApp(context, item.appPackage) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationAccessBanner(context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.NotificationsOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Notification Access: Disabled ○",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "اضغط لفتح الإعدادات الرسمية وتفعيل الصلاحية",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Button(
                onClick = { NotificationAccessHelper.openNotificationAccessSettings(context) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Settings", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationItem,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit
) {
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(item.timestamp))
    val elapsedMinutes = ((System.currentTimeMillis() - item.timestamp) / (1000 * 60)).coerceAtLeast(0)
    val elapsedStr = when {
        elapsedMinutes < 1 -> "Just now"
        elapsedMinutes < 60 -> "${elapsedMinutes}m ago"
        else -> "${elapsedMinutes / 60}h ago"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onMarkRead
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.appName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                if (item.isSilentMode || item.isDND) {
                    Icon(
                        Icons.Rounded.VolumeOff,
                        contentDescription = "Silent/DND",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$timeStr • $elapsedStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (item.title.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (item.content.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NotifPriorityBadge(item.priority)
                TextButton(
                    onClick = onOpen,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Open App", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

private fun openApp(context: Context, packageName: String) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
