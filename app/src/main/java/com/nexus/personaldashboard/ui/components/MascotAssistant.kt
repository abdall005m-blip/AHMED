package com.nexus.personaldashboard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nexus.personaldashboard.ui.navigation.NavRoute
import kotlinx.coroutines.delay

@Composable
fun MascotAssistant(onNavigate: (NavRoute) -> Unit, onDismiss: () -> Unit) {
    var userInput by remember { mutableStateOf("") }
    var assistantResponse by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val transition = rememberInfiniteTransition(label = "mascot")
    val blinkScale by transition.animateFloat(1f, .95f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), "blink")

    fun handleQuery(q: String) {
        isThinking = true
        val route = when {
            q.contains("game", true) || q.contains("play", true) || q.contains("العاب", true) -> NavRoute.GAMES
            q.contains("chat", true) || q.contains("شات", true) || q.contains("رسالة", true) -> NavRoute.PRIVATE_CHAT
            q.contains("islam", true) || q.contains("quran", true) || q.contains("قرآن", true) || q.contains("اسلامي", true) -> NavRoute.ISLAMIC
            q.contains("task", true) || q.contains("مهام", true) || q.contains("todo", true) -> NavRoute.TASKS
            q.contains("mood", true) || q.contains("مزاج", true) -> NavRoute.MOOD
            q.contains("schedule", true) || q.contains("جدول", true) -> NavRoute.SCHEDULE
            q.contains("notif", true) || q.contains("اشعار", true) -> NavRoute.NOTIFICATIONS
            q.contains("setting", true) || q.contains("اعداد", true) -> NavRoute.SETTINGS
            q.contains("coin", true) || q.contains("store", true) || q.contains("متجر", true) -> NavRoute.COIN_STORE
            q.contains("entertain", true) || q.contains("ترفيه", true) -> NavRoute.ENTERTAINMENT
            else -> null
        }
        assistantResponse = if (route != null) "✅ جاري فتح ${route.name.lowercase().replace('_', ' ')}!" else "مش فاهم 😅 جرب: افتح الألعاب، الشات، الإسلامي، المهام، أو المزاج"
    }

    LaunchedEffect(isThinking) { if (isThinking) { delay(700); isThinking = false } }
    LaunchedEffect(Unit) { delay(150); focusRequester.requestFocus() }

    Dialog(onDismissRequest = onDismiss) {
        Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface), CardDefaults.cardElevation(8.dp)) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(80.dp).scale(blinkScale).clip(CircleShape).background(Brush.radialGradient(listOf(Color(0xFF9333EA), Color(0xFF6B21A8)))), Alignment.Center) {
                    if (isThinking) CircularProgressIndicator(Modifier.size(24.dp), Color.White, strokeWidth = 2.dp)
                    else Text("🤖", fontSize = 36.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text(if (isThinking) "بفكر... 🤔" else "أنا Nexus AI 👋", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                if (assistantResponse.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(12.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer.copy(.5f))) {
                        Text(assistantResponse, Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                } else {
                    Text("جرب:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    listOf(listOf("افتح الألعاب","الشات","الإسلامي"), listOf("المهام","المزاج","الإعدادات")).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { s -> SuggestionChip({ userInput = s }, { Text(s, fontSize = 10.sp) }, Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = userInput, onValueChange = { userInput = it },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    placeholder = { Text("اسأل أي حاجة...") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        keyboardController?.hide()
                        if (userInput.isNotBlank()) { handleQuery(userInput.trim()); userInput = "" }
                    }),
                    trailingIcon = {
                        IconButton({
                            keyboardController?.hide()
                            if (userInput.isNotBlank() && !isThinking) { handleQuery(userInput.trim()); userInput = "" }
                        }) { Icon(Icons.Rounded.Send, null) }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onDismiss) { Text("إغلاق") }
            }
        }
    }
}
