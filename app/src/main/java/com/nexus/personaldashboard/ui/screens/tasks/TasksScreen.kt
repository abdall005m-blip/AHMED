package com.nexus.personaldashboard.ui.screens.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.personaldashboard.ui.components.SectionBackground

data class SimpleTask(val id: Int, val title: String, var done: Boolean = false, val priority: Int = 1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(onBack: () -> Unit) {
    val tasks = remember {
        mutableStateListOf(
            SimpleTask(1, "مراجعة المشروع", priority = 2),
            SimpleTask(2, "الرد على الرسائل", priority = 1),
            SimpleTask(3, "التمرين اليومي", priority = 1),
            SimpleTask(4, "قراءة كتاب", priority = 0),
        )
    }
    var newTask by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("مهمة جديدة ✅") },
            text = {
                OutlinedTextField(
                    value = newTask, onValueChange = { newTask = it },
                    label = { Text("اسم المهمة") }, shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newTask.isNotBlank()) {
                        tasks.add(SimpleTask(tasks.size + 1, newTask.trim()))
                        newTask = ""
                    }
                    showAddDialog = false
                }) { Text("إضافة") }
            },
            dismissButton = { TextButton({ showAddDialog = false; newTask = "" }) { Text("إلغاء") } }
        )
    }

    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "tasks")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("المهام ✅", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                actions = {
                    IconButton({ showAddDialog = true }) { Icon(Icons.Rounded.Add, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            // Stats
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val done = tasks.count { it.done }
                StatChip("الكل ${tasks.size}", Color(0xFF6B7280))
                StatChip("تم $done", Color(0xFF22C55E))
                StatChip("باقي ${tasks.size - done}", Color(0xFFF59E0B))
            }
            if (tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅", fontSize = 56.sp)
                        Text("مفيش مهام دلوقتي", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Button({ showAddDialog = true }, shape = RoundedCornerShape(12.dp)) { Text("+ أضف مهمة") }
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tasks, key = { it.id }) { task ->
                        val priorityColor = listOf(Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444))[task.priority.coerceIn(0, 2)]
                        Card(Modifier.fillMaxWidth(), RoundedCornerShape(14.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = task.done, onCheckedChange = {
                                    val index = tasks.indexOf(task)
                                    if (index >= 0) tasks[index] = task.copy(done = it)
                                })
                                Box(Modifier.size(8.dp).then(Modifier).padding(end = 8.dp)) {
                                    Surface(CircleShape, color = priorityColor) { Box(Modifier.size(8.dp)) }
                                }
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    task.title, style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (task.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { tasks.remove(task) }) {
                                    Icon(Icons.Rounded.Delete, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun StatChip(label: String, color: Color) {
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(.15f)) {
        Text(label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}
