package com.nexus.personaldashboard.ui.screens.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.nexus.personaldashboard.domain.model.Schedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScheduleScreen(
    scheduleId: Long = 0L,
    viewModel: ScheduleViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val schedules by viewModel.allSchedules.collectAsStateWithLifecycle()
    val existing = schedules.find { it.id == scheduleId }
    val isEditing = scheduleId != 0L

    var title by remember(existing) { mutableStateOf(existing?.title ?: "") }
    var day by remember(existing) { mutableIntStateOf(existing?.dayOfWeek ?: 1) }
    var note by remember(existing) { mutableStateOf(existing?.note ?: "") }
    var icon by remember(existing) { mutableStateOf(existing?.iconName ?: "📌") }
    var repeatType by remember(existing) { mutableStateOf(existing?.repeatType ?: RepeatType.NONE) }
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Schedule" else "New Schedule",
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
                label = { Text("Schedule Title *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = icon,
                onValueChange = { icon = it },
                label = { Text("Icon Emoji (e.g. 🏋️‍♂️, 📚, 💼)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Day of Week", style = MaterialTheme.typography.labelLarge)
            daysOfWeek.forEachIndexed { index, dayName ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = day == index + 1,
                        onClick = { day = index + 1 }
                    )
                    Text(dayName, modifier = Modifier.padding(start = 8.dp))
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

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    val schedule = Schedule(
                        id = if (isEditing) scheduleId else 0L,
                        title = title.trim(),
                        dayOfWeek = day,
                        startTime = System.currentTimeMillis(),
                        endTime = System.currentTimeMillis() + 3600000,
                        repeatType = repeatType,
                        note = note.trim(),
                        iconName = icon.ifBlank { "📌" }
                    )
                    viewModel.upsert(schedule)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = title.isNotBlank()
            ) {
                Text(
                    if (isEditing) "Save Changes" else "Create Schedule",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
