package com.nexus.personaldashboard.ui.screens.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.personaldashboard.domain.model.RepeatType
import com.nexus.personaldashboard.domain.model.Task
import com.nexus.personaldashboard.domain.model.TaskPriority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: Long = 0L,
    viewModel: TasksViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val upcomingTasks by viewModel.upcomingTasks.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasks.collectAsStateWithLifecycle()
    val allTasks = todayTasks + upcomingTasks + completedTasks
    val existingTask = allTasks.find { it.id == taskId }

    var title by remember(existingTask) { mutableStateOf(existingTask?.title ?: "") }
    var description by remember(existingTask) { mutableStateOf(existingTask?.description ?: "") }
    var priority by remember(existingTask) { mutableStateOf(existingTask?.priority ?: TaskPriority.MEDIUM) }
    var repeatType by remember(existingTask) { mutableStateOf(existingTask?.repeatType ?: RepeatType.NONE) }
    var reminderEnabled by remember(existingTask) { mutableStateOf(existingTask?.reminderEnabled ?: false) }
    val isEditing = taskId != 0L

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Task" else "New Task",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text("Priority", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskPriority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.name) }
                    )
                }
            }

            Text("Repeat", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RepeatType.values().forEach { r ->
                    FilterChip(
                        selected = repeatType == r,
                        onClick = { repeatType = r },
                        label = { Text(r.name) }
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(
                        Icons.Rounded.Alarm,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Task Reminder", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text("Notify when task is due", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    val task = Task(
                        id = if (isEditing) taskId else 0L,
                        title = title.trim(),
                        description = description.trim(),
                        dueDate = System.currentTimeMillis() + 3600000,
                        priority = priority,
                        repeatType = repeatType,
                        reminderEnabled = reminderEnabled
                    )
                    viewModel.upsertTask(task)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = title.isNotBlank()
            ) {
                Text(
                    if (isEditing) "Save Changes" else "Create Task",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
