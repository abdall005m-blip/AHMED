package com.nexus.personaldashboard.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarRacingScreen(onBack: () -> Unit) {
    var playerLane by remember { mutableStateOf(1) } // 0,1,2
    var score by remember { mutableStateOf(0) }
    var gameRunning by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }
    var obstaclePositions by remember { mutableStateOf(listOf<Pair<Int, Float>>()) } // lane, y
    var roadOffset by remember { mutableStateOf(0f) }

    // Game loop
    LaunchedEffect(gameRunning) {
        if (gameRunning) {
            while (gameRunning && !gameOver) {
                delay(50)
                roadOffset = (roadOffset + 5f) % 60f
                // Move obstacles
                obstaclePositions = obstaclePositions.map { (lane, y) -> lane to y + 5f }
                    .filter { (_, y) -> y < 1000f }
                // Add new obstacles
                if (obstaclePositions.size < 3 && (score % 3 == 0)) {
                    obstaclePositions = obstaclePositions + (listOf(0, 1, 2).random() to -60f)
                }
                score++
                // Collision
                val collision = obstaclePositions.any { (lane, y) -> lane == playerLane && y in 600f..700f }
                if (collision) { gameOver = true; gameRunning = false }
            }
        }
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        TopAppBar(
            title = { Text("سباق السيارات 🏎️", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (!gameRunning && !gameOver) {
                // Start screen
                Spacer(Modifier.weight(1f))
                Text("🏎️", fontSize = 80.sp)
                Spacer(Modifier.height(16.dp))
                Text("سباق السيارات", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("تحاشى العوائق!", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))
                Button({ gameRunning = true; obstaclePositions = emptyList(); score = 0 }, shape = RoundedCornerShape(16.dp)) {
                    Text("ابدأ اللعبة 🚀", fontSize = 18.sp)
                }
                Spacer(Modifier.weight(1f))
            } else if (gameOver) {
                Spacer(Modifier.weight(1f))
                Text("💥 اصطدمت!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text("نقاطك: $score", style = MaterialTheme.typography.headlineMedium)
                Text("مكسبت: ${score / 10} 🪙", style = MaterialTheme.typography.titleLarge, color = Color(0xFFF59E0B))
                Spacer(Modifier.height(24.dp))
                Button({ gameOver = false; gameRunning = true; obstaclePositions = emptyList(); score = 0 }, shape = RoundedCornerShape(16.dp)) { Text("العب تاني") }
                Spacer(Modifier.weight(1f))
            } else {
                // Game UI
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("نقاط: $score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("🪙 ${score / 10}", style = MaterialTheme.typography.titleMedium, color = Color(0xFFF59E0B))
                }
                Spacer(Modifier.height(16.dp))
                // Road
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF374151))
                ) {
                    // Road lanes
                    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        repeat(3) { lane ->
                            Box(Modifier.weight(1f).fillMaxHeight()) {
                                // Lane dividers
                                if (lane < 2) {
                                    Box(Modifier.width(2.dp).fillMaxHeight().align(Alignment.CenterEnd).background(Color.White.copy(.3f)))
                                }
                                // Player car
                                if (lane == playerLane) {
                                    Text("🏎️", modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-40).dp), fontSize = 36.sp)
                                }
                                // Obstacles
                                obstaclePositions.filter { (l, _) -> l == lane }.forEach { (_, y) ->
                                    Text("🚧", modifier = Modifier.align(Alignment.TopCenter).offset(y = y.dp), fontSize = 32.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Controls
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = { if (playerLane > 0) playerLane-- },
                        enabled = playerLane > 0,
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("◀", fontSize = 24.sp) }
                    Button(
                        onClick = { if (playerLane < 2) playerLane++ },
                        enabled = playerLane < 2,
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("▶", fontSize = 24.sp) }
                }
            }
        }
    }
}
