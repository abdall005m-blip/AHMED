package com.nexus.personaldashboard.ui.screens.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.personaldashboard.ui.components.SectionBackground
import java.text.SimpleDateFormat
import java.util.*

data class MoodEntry(val emoji: String, val mood: String, val note: String, val timestamp: Long)

data class MoodOption(val emoji: String, val nameAr: String, val color: Color)

val moodOptions = listOf(
    MoodOption("😄", "مبسوط", Color(0xFF22C55E)),
    MoodOption("😢", "زعلان", Color(0xFF3B82F6)),
    MoodOption("😐", "هادي", Color(0xFF6B7280)),
    MoodOption("😴", "زهقان", Color(0xFF8B5CF6)),
    MoodOption("🥰", "محب", Color(0xFFEC4899)),
    MoodOption("😠", "متضايق", Color(0xFFEF4444)),
    MoodOption("🤩", "متحمس", Color(0xFFF59E0B)),
    MoodOption("😰", "قلقان", Color(0xFF06B6D4)),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(onBack: () -> Unit) {
    var selectedMood by remember { mutableStateOf<MoodOption?>(null) }
    var noteText by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<MoodEntry>() }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            SectionBackground(section = "mood")
            LazyColumn(
                Modifier.fillMaxSize().statusBarsPadding().imePadding(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) }
                        Text("المزاج 💖", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
                item {
                    Text("ايه مزاجك دلوقتي؟", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("How are you feeling?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item {
                    // Mood Grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        moodOptions.chunked(4).forEach { row ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { mood ->
                                    val isSelected = selectedMood == mood
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) mood.color.copy(.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                            .border(if (isSelected) 2.dp else 0.dp, mood.color, RoundedCornerShape(16.dp))
                                            .clickable { selectedMood = mood },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(mood.emoji, fontSize = 28.sp)
                                            Text(mood.nameAr, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("ملاحظة (اختياري)") },
                        placeholder = { Text("اكتب ايه اللي في بالك...") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
                item {
                    Button(
                        onClick = {
                            if (selectedMood != null) {
                                history.add(0, MoodEntry(selectedMood!!.emoji, selectedMood!!.nameAr, noteText, System.currentTimeMillis()))
                                noteText = ""
                                selectedMood = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedMood != null,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("💾 احفظ المزاج", fontSize = 16.sp)
                    }
                }
                if (history.isNotEmpty()) {
                    item {
                        Text("السجل", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    items(history.size.coerceAtMost(5)) { i ->
                        val entry = history[i]
                        val date = SimpleDateFormat("dd/MM - hh:mm a", Locale.getDefault()).format(Date(entry.timestamp))
                        Card(Modifier.fillMaxWidth(), RoundedCornerShape(12.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(entry.emoji, fontSize = 32.sp)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(entry.mood, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    if (entry.note.isNotBlank()) Text(entry.note, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                    Text(date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
