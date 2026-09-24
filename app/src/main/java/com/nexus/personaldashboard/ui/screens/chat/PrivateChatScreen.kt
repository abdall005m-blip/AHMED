package com.nexus.personaldashboard.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.personaldashboard.ui.components.SectionBackground
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class ChatMessage(val id: String, val text: String, val isMe: Boolean, val timestamp: Long)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatScreen(onBack: () -> Unit) {
    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "هلاو! 👋 عامل ايه؟", false, System.currentTimeMillis() - 300000),
            ChatMessage("2", "بخير الحمد لله! انت عامل ايه؟ 😊", true, System.currentTimeMillis() - 240000),
            ChatMessage("3", "تمام جداً 💕 بفتكرك كتير", false, System.currentTimeMillis() - 180000),
            ChatMessage("4", "انا كمان 🥰 مشتاق ليك", true, System.currentTimeMillis() - 120000),
            ChatMessage("5", "انت احلى واحد في الدنيا ❤️", false, System.currentTimeMillis() - 60000),
        )
    }
    var inputText by remember { mutableStateOf("") }
    var showEmojiRow by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    // Calculate heart days from start date
    val startDate = Calendar.getInstance().apply { set(2024, 0, 1) }.timeInMillis
    val daysTogether = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - startDate)

    fun sendMessage() {
        if (inputText.isBlank()) return
        messages.add(ChatMessage(UUID.randomUUID().toString(), inputText.trim(), true, System.currentTimeMillis()))
        inputText = ""
        keyboardController?.hide()
        scope.launch { listState.animateScrollToItem(messages.size - 1) }
    }

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            SectionBackground(section = "chat")
            Column(Modifier.fillMaxSize().statusBarsPadding().imePadding()) {
                // Header
                Surface(shadowElevation = 2.dp, color = MaterialTheme.colorScheme.surface.copy(.9f)) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) }
                        // Avatar
                        Box(Modifier.size(44.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Color(0xFFEC4899), Color(0xFFBE185D)))), Alignment.Center) {
                            Text("💕", fontSize = 22.sp)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("My Love 💕", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF22C55E)))
                                Spacer(Modifier.width(4.dp))
                                Text("Online", style = MaterialTheme.typography.labelSmall, color = Color(0xFF22C55E))
                            }
                        }
                        // Heart counter
                        Box(
                            Modifier.clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFEC4899).copy(.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("❤️ $daysTogether يوم", style = MaterialTheme.typography.labelMedium, color = Color(0xFFEC4899), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        MessageBubble(msg)
                    }
                }

                // Emoji row
                if (showEmojiRow) {
                    Row(
                        Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("❤️","😍","🥰","😘","💕","💖","✨","🎉").forEach { em ->
                            Text(em, fontSize = 24.sp, modifier = Modifier.clickable(onClick = { inputText += em }))
                        }
                    }
                }

                // Input area
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        IconButton({ showEmojiRow = !showEmojiRow }) {
                            Text(if (showEmojiRow) "⌨️" else "😊", fontSize = 22.sp)
                        }
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("اكتب رسالة...") },
                            maxLines = 4,
                            shape = RoundedCornerShape(24.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { sendMessage() })
                        )
                        Spacer(Modifier.width(4.dp))
                        IconButton({ showEmojiRow = false }, modifier = Modifier.clip(CircleShape).background(Color(0xFFEC4899)).size(48.dp)) {
                            Icon(Icons.Rounded.Send, null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }
}

@Composable
fun MessageBubble(msg: ChatMessage) {
    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start
    ) {
        Box(
            Modifier
                .widthIn(max = 270.dp)
                .clip(RoundedCornerShape(
                    topStart = 20.dp, topEnd = 20.dp,
                    bottomStart = if (msg.isMe) 20.dp else 4.dp,
                    bottomEnd = if (msg.isMe) 4.dp else 20.dp
                ))
                .background(
                    if (msg.isMe) Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899)))
                    else Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(msg.text, style = MaterialTheme.typography.bodyMedium, color = if (msg.isMe) Color.White else MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.height(2.dp))
        Text(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 4.dp))
    }
}

