package com.nexus.personaldashboard.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XOGameScreen(onBack: () -> Unit) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var gameOver by remember { mutableStateOf(false) }
    var coinsWon by remember { mutableStateOf(0) }

    fun checkWinner(b: List<String>): String? {
        val wins = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )
        return wins.firstOrNull { (a, b2, c) -> b[a].isNotEmpty() && b[a] == b[b2] && b[b2] == b[c] }?.let { (a, _, _) -> b[a] }
    }

    fun onCellClick(index: Int) {
        if (gameOver || board[index].isNotEmpty()) return
        val newBoard = board.toMutableList().also { it[index] = currentPlayer }
        board = newBoard
        val w = checkWinner(newBoard)
        if (w != null) {
            winner = w; gameOver = true; coinsWon = 10
        } else if (newBoard.all { it.isNotEmpty() }) {
            winner = "Draw"; gameOver = true
        } else {
            currentPlayer = if (currentPlayer == "X") "O" else "X"
        }
    }

    fun resetGame() {
        board = List(9) { "" }; currentPlayer = "X"; winner = null; gameOver = false; coinsWon = 0
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        TopAppBar(
            title = { Text("XO ❌⭕", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
            actions = { IconButton(::resetGame) { Icon(Icons.Rounded.Refresh, null) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            // Status
            AnimatedContent(gameOver, label = "status") { over ->
                if (over) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (winner == "Draw") "تعادل! 🤝" else "فاز $winner! 🎉",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (coinsWon > 0) Text("+$coinsWon 🪙", style = MaterialTheme.typography.titleLarge, color = Color(0xFFF59E0B))
                        Spacer(Modifier.height(8.dp))
                        Button(::resetGame, shape = RoundedCornerShape(16.dp)) { Text("لعبة جديدة") }
                    }
                } else {
                    Text(
                        "دور اللاعب ${if (currentPlayer == "X") "❌" else "⭕"}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            // Board
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 0..2) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            val cell = board[index]
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        when (cell) {
                                            "X" -> Color(0xFFDC2626).copy(.15f)
                                            "O" -> Color(0xFF2563EB).copy(.15f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(.3f), RoundedCornerShape(16.dp))
                                    .clickable(enabled = !gameOver) { onCellClick(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (cell) { "X" -> "❌"; "O" -> "⭕"; else -> "" },
                                    fontSize = 40.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
