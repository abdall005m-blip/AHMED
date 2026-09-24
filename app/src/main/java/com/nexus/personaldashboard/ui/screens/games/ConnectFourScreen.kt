package com.nexus.personaldashboard.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectFourScreen(onBack: () -> Unit) {
    val rows = 6; val cols = 7
    var board by remember { mutableStateOf(Array(rows) { Array(cols) { 0 } }) } // 0=empty, 1=red, 2=yellow
    var currentPlayer by remember { mutableStateOf(1) }
    var winner by remember { mutableStateOf(0) }
    var isDraw by remember { mutableStateOf(false) }

    fun checkWin(b: Array<Array<Int>>, p: Int): Boolean {
        for (r in 0 until rows) for (c in 0 until cols) {
            if (c + 3 < cols && (0..3).all { b[r][c + it] == p }) return true
            if (r + 3 < rows && (0..3).all { b[r + it][c] == p }) return true
            if (r + 3 < rows && c + 3 < cols && (0..3).all { b[r + it][c + it] == p }) return true
            if (r + 3 < rows && c - 3 >= 0 && (0..3).all { b[r + it][c - it] == p }) return true
        }
        return false
    }

    fun dropPiece(col: Int) {
        if (winner != 0 || isDraw) return
        val newBoard = board.map { it.clone() }.toTypedArray()
        val row = (rows - 1 downTo 0).firstOrNull { newBoard[it][col] == 0 } ?: return
        newBoard[row][col] = currentPlayer
        board = newBoard
        if (checkWin(newBoard, currentPlayer)) {
            winner = currentPlayer
        } else if (newBoard.all { r -> r.all { it != 0 } }) {
            isDraw = true
        } else {
            currentPlayer = if (currentPlayer == 1) 2 else 1
        }
    }

    fun reset() {
        board = Array(rows) { Array(cols) { 0 } }
        currentPlayer = 1; winner = 0; isDraw = false
    }

    val playerColor = { p: Int -> when (p) { 1 -> Color(0xFFDC2626); 2 -> Color(0xFFEAB308); else -> MaterialTheme.colorScheme.surfaceVariant } }
    val playerEmoji = { p: Int -> if (p == 1) "🔴 لاعب 1" else "🟡 لاعب 2" }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        TopAppBar(
            title = { Text("اربع في صف 🟡🔴", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
            actions = { IconButton(::reset) { Icon(Icons.Rounded.Refresh, null) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            // Status
            when {
                winner != 0 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉 فاز ${playerEmoji(winner)}!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("+20 🪙", style = MaterialTheme.typography.titleLarge, color = Color(0xFFF59E0B))
                    Spacer(Modifier.height(8.dp))
                    Button(::reset, shape = RoundedCornerShape(16.dp)) { Text("لعبة جديدة") }
                }
                isDraw -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("تعادل! 🤝", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Button(::reset, shape = RoundedCornerShape(16.dp)) { Text("لعبة جديدة") }
                }
                else -> Text("دور ${playerEmoji(currentPlayer)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            // Board
            Box(Modifier.clip(RoundedCornerShape(16.dp)).background(Color(0xFF1E3A5F)).padding(8.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (row in 0 until rows) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (col in 0 until cols) {
                                val cell = board[row][col]
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (cell == 0) Color(0xFF0F2A47) else playerColor(cell))
                                        .let { if (row == 0 && winner == 0 && !isDraw) it.clickable { dropPiece(col) } else it },
                                    contentAlignment = Alignment.Center
                                ) {}
                            }
                        }
                    }
                }
            }
            // Column tap buttons
            if (winner == 0 && !isDraw) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (col in 0 until cols) {
                        Box(
                            Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                                .background(playerColor(currentPlayer).copy(.2f))
                                .clickable { dropPiece(col) },
                            Alignment.Center
                        ) { Text("▼", fontSize = 14.sp, color = playerColor(currentPlayer)) }
                    }
                }
            }
        }
    }
}
