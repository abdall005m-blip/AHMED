package com.nexus.personaldashboard.ui.screens.aihub

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.personaldashboard.ui.components.SectionBackground

data class AITool(val emoji: String, val name: String, val description: String, val url: String, val color: Color)

val aiTools = listOf(
    AITool("🤖", "ChatGPT", "أقوى نموذج لغوي من OpenAI", "https://chat.openai.com", Color(0xFF10A37F)),
    AITool("🔮", "Gemini", "مساعد الذكاء الاصطناعي من Google", "https://gemini.google.com", Color(0xFF4285F4)),
    AITool("🎨", "DALL-E", "توليد الصور بالذكاء الاصطناعي", "https://openai.com/dall-e", Color(0xFFEA4335)),
    AITool("📝", "Claude", "مساعد الذكاء من Anthropic", "https://claude.ai", Color(0xFFB55A30)),
    AITool("🔊", "ElevenLabs", "تحويل النص لصوت بشري", "https://elevenlabs.io", Color(0xFF7C3AED)),
    AITool("🖼️", "Midjourney", "رسم احترافي بالذكاء الاصطناعي", "https://midjourney.com", Color(0xFF1A1A2E)),
    AITool("📖", "Perplexity", "بحث ذكي بالذكاء الاصطناعي", "https://perplexity.ai", Color(0xFF20B2AA)),
    AITool("💻", "GitHub Copilot", "مساعد البرمجة الذكي", "https://github.com/features/copilot", Color(0xFF24292E)),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIHubScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "ai")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("مركز الذكاء 🤖", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            // Header banner
            Box(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF06B6D4), Color(0xFF7C3AED))))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 36.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("أدوات الذكاء الاصطناعي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("اختار الأداة المناسبة واستخدمها مباشرة", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(.85f))
                    }
                }
            }
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(aiTools.size) { i ->
                    val tool = aiTools[i]
                    Card(
                        Modifier.fillMaxWidth().clickable { uriHandler.openUri(tool.url) },
                        RoundedCornerShape(14.dp),
                        CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                        CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(tool.color),
                                Alignment.Center
                            ) { Text(tool.emoji, fontSize = 24.sp) }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(tool.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(tool.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Rounded.OpenInNew, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
