package com.nexus.personaldashboard.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ChangeVibeButton(modifier: Modifier = Modifier) {
    var showAnimation by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899), Color(0xFF3B82F6))))
                .clickable { showAnimation = true }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("✨", fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text("غيّرلي الجو  •  Change the Vibe", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("🤖", fontSize = 16.sp)
        }
    }
    if (showAnimation) ChangeVibeAnimation { showAnimation = false }
}

@Composable
fun ChangeVibeAnimation(onComplete: () -> Unit) {
    var phase by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        delay(3000); phase = 1
        delay(7000); phase = 2
        delay(3000); phase = 3
        delay(2000); onComplete()
    }
    Box(Modifier.fillMaxSize().background(Color.Black.copy(.8f)), Alignment.Center) {
        AnimatedContent(phase, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "vibe") { p ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (p) {
                    0 -> { Text("🤖🤖🤖", fontSize = 48.sp); Text("الروبوتات وصلت!", style = MaterialTheme.typography.titleLarge, color = Color.White) }
                    1 -> { Text("🧹🤖🪣", fontSize = 48.sp); Text("بيعدّلوا الجو...", style = MaterialTheme.typography.titleLarge, color = Color.White); LinearProgressIndicator(Modifier.fillMaxWidth(.6f), color = Color(0xFF8B5CF6)) }
                    2 -> { Text("🫧", fontSize = 64.sp); Text("الفقاعة بتكبر...", style = MaterialTheme.typography.titleLarge, color = Color.White) }
                    else -> { Text("🎉", fontSize = 64.sp); Text("الجو اتغيّر!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White) }
                }
            }
        }
    }
}
