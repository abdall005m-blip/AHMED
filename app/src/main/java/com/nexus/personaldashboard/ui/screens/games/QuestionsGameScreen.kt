package com.nexus.personaldashboard.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class Question(val text: String, val options: List<String>, val correctIndex: Int)

val questions = listOf(
    Question("عاصمة مصر؟", listOf("الإسكندرية", "القاهرة", "أسوان", "الغردقة"), 1),
    Question("كم عدد أيام السنة؟", listOf("300", "356", "365", "360"), 2),
    Question("كم يساوي 7 × 8؟", listOf("54", "56", "58", "64"), 1),
    Question("أكبر كوكب في المجموعة الشمسية؟", listOf("زحل", "المريخ", "المشتري", "الأرض"), 2),
    Question("لغة البرمجة اللي بنكتب بيها التطبيق ده؟", listOf("Java", "Python", "Kotlin", "Swift"), 2),
    Question("ما هو اليوم الأول في الأسبوع إسلامياً؟", listOf("الاثنين", "الجمعة", "الأحد", "السبت"), 2),
    Question("عدد سور القرآن الكريم؟", listOf("100", "110", "114", "120"), 2),
    Question("من هو مخترع الهاتف؟", listOf("توماس إديسون", "ألكسندر جراهام بيل", "نيوتن", "أينشتاين"), 1),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsGameScreen(onBack: () -> Unit) {
    var questionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf(-1) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(15) }
    var showResult by remember { mutableStateOf(false) }
    var gameFinished by remember { mutableStateOf(false) }

    val currentQ = questions.getOrNull(questionIndex)

    LaunchedEffect(questionIndex, showResult) {
        if (!showResult && !gameFinished && currentQ != null) {
            timeLeft = 15
            while (timeLeft > 0) {
                delay(1000); timeLeft--
            }
            if (selectedAnswer == -1) showResult = true
        }
    }

    LaunchedEffect(showResult) {
        if (showResult) {
            delay(2000)
            if (questionIndex + 1 < questions.size) {
                questionIndex++; selectedAnswer = -1; showResult = false
            } else {
                gameFinished = true
            }
        }
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        TopAppBar(
            title = { Text("الأسئلة ❓", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        if (gameFinished) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("🎉 انتهت اللعبة!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text("نتيجتك: $score / ${questions.size}", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text("مكسبت: ${score * 25} 🪙", style = MaterialTheme.typography.titleLarge, color = Color(0xFFF59E0B))
                Spacer(Modifier.height(24.dp))
                Button({ questionIndex = 0; score = 0; selectedAnswer = -1; showResult = false; gameFinished = false }, shape = RoundedCornerShape(16.dp)) { Text("العب تاني") }
            }
        } else if (currentQ != null) {
            Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Progress
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("سؤال ${questionIndex + 1} / ${questions.size}", style = MaterialTheme.typography.labelLarge)
                    Text("⏱ $timeLeft ث", style = MaterialTheme.typography.labelLarge, color = if (timeLeft <= 5) Color.Red else MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(progress = { timeLeft / 15f }, modifier = Modifier.fillMaxWidth(), color = if (timeLeft <= 5) Color.Red else MaterialTheme.colorScheme.primary)
                Text("${score} 🪙", style = MaterialTheme.typography.labelMedium, color = Color(0xFFF59E0B))
                // Question
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)) {
                    Text(currentQ.text, Modifier.padding(20.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
                // Options
                currentQ.options.forEachIndexed { i, opt ->
                    val bg = when {
                        !showResult -> if (selectedAnswer == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        i == currentQ.correctIndex -> Color(0xFF22C55E)
                        selectedAnswer == i -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(bg)
                            .let { if (!showResult) it.clickable { selectedAnswer = i; if (i == currentQ.correctIndex) score++; showResult = true } else it }
                            .padding(16.dp)
                    ) {
                        Text(opt, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium,
                            color = if (bg == MaterialTheme.colorScheme.surfaceVariant) MaterialTheme.colorScheme.onSurface else Color.White)
                    }
                }
                if (showResult) {
                    Text(
                        if (selectedAnswer == currentQ.correctIndex) "✅ صح! ممتاز!" else if (selectedAnswer == -1) "⏰ الوقت انتهى!" else "❌ غلط! الإجابة الصح: ${currentQ.options[currentQ.correctIndex]}",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                        color = if (selectedAnswer == currentQ.correctIndex) Color(0xFF22C55E) else Color(0xFFEF4444),
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
