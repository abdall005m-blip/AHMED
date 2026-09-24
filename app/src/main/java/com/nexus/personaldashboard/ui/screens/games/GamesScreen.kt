package com.nexus.personaldashboard.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
import com.nexus.personaldashboard.ui.components.SectionBackground
import com.nexus.personaldashboard.ui.navigation.NavRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(onNavigate: (NavRoute) -> Unit, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "games")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("Games 🎮", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                actions = {
                    Box(Modifier.clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 12.dp, vertical = 4.dp)) {
                        Text("🪙 500", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    GameCard("❌⭕", "XO", "Tic-Tac-Toe", listOf(Color(0xFFDC2626), Color(0xFF991B1B)), "+10 🪙", Modifier.weight(1f)) { onNavigate(NavRoute.GAME_XO) }
                    GameCard("🏎️", "سباق السيارات", "Car Racing", listOf(Color(0xFFF97316), Color(0xFFEA580C)), "+15 🪙", Modifier.weight(1f)) { onNavigate(NavRoute.GAME_RACING) }
                }
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    GameCard("🟡🔴", "اربع في صف", "Connect Four", listOf(Color(0xFF7C3AED), Color(0xFF4338CA)), "+20 🪙", Modifier.weight(1f)) { onNavigate(NavRoute.GAME_CONNECT_FOUR) }
                    GameCard("❓", "الأسئلة", "Questions", listOf(Color(0xFF0891B2), Color(0xFF0E7490)), "+25 🪙", Modifier.weight(1f)) { onNavigate(NavRoute.GAME_QUESTIONS) }
                }
            }
        }
    }
}

@Composable
fun GameCard(emoji: String, titleAr: String, titleEn: String, gradient: List<Color>, reward: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(gradient))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(emoji, fontSize = 36.sp)
            Column {
                Text(titleAr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(titleEn, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(.7f))
                Spacer(Modifier.height(4.dp))
                Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White.copy(.2f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text(reward, style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
