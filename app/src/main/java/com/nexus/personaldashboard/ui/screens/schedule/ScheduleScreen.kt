package com.nexus.personaldashboard.ui.screens.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.personaldashboard.domain.model.Schedule
import com.nexus.personaldashboard.ui.components.EmptyState
import com.nexus.personaldashboard.ui.components.GlassCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val schedules by viewModel.allSchedules.collectAsStateWithLifecycle()
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selectedDay by remember { mutableIntStateOf(viewModel.todayDayOfWeek) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule 📅", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add Schedule",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = (selectedDay - 1).coerceIn(0, 6),
                containerColor = MaterialTheme.colorScheme.background,
                edgePadding = 16.dp
            ) {
                daysOfWeek.forEachIndexed { index, day ->
                    Tab(
                        selected = selectedDay == index + 1,
                        onClick = { selectedDay = index + 1 },
                        text = { Text(day) }
                    )
                }
            }

            val filtered = schedules.filter { it.dayOfWeek == selectedDay }

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        emoji = "📅",
                        title = "No schedules for this day",
                        subtitle = "Tap + to add classes, meetings, or workouts"
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { schedule ->
                        ScheduleItemCard(
                            schedule = schedule,
                            onEdit = { onEdit(schedule.id) },
                            onDelete = { viewModel.delete(schedule) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleItemCard(
    schedule: Schedule,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
    var showMenu by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = schedule.iconName.ifBlank { "📌" },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${timeFmt.format(Date(schedule.startTime))} — ${timeFmt.format(Date(schedule.endTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                if (schedule.note.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = schedule.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        contentDescription = "Options",
                        modifier = Modifier.size(18.dp)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Rounded.Edit, null) },
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Delete,
                                null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
