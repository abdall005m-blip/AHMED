package com.nexus.personaldashboard.ui.screens.islamic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.delay
import java.util.*

data class Prayer(val nameAr: String, val nameEn: String, val hour: Int, val minute: Int, val emoji: String)

val cairoDefaultPrayers = listOf(
    Prayer("الفجر", "Fajr", 4, 45, "🌅"),
    Prayer("الشروق", "Sunrise", 6, 15, "☀️"),
    Prayer("الظهر", "Dhuhr", 12, 15, "🌤️"),
    Prayer("العصر", "Asr", 15, 30, "🌇"),
    Prayer("المغرب", "Maghrib", 18, 10, "🌆"),
    Prayer("العشاء", "Isha", 19, 40, "🌙"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(onBack: () -> Unit) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) { delay(1000); currentTime = System.currentTimeMillis() }
    }

    val cal = Calendar.getInstance().apply { timeInMillis = currentTime }
    val nowH = cal.get(Calendar.HOUR_OF_DAY)
    val nowM = cal.get(Calendar.MINUTE)
    val nowTotalMin = nowH * 60 + nowM

    fun prayerTotalMin(p: Prayer) = p.hour * 60 + p.minute
    fun countdownMin(p: Prayer): Int {
        val diff = prayerTotalMin(p) - nowTotalMin
        return if (diff < 0) diff + 24 * 60 else diff
    }

    val nextPrayer = cairoDefaultPrayers.filter { countdownMin(it) > 0 }.minByOrNull { countdownMin(it) }
        ?: cairoDefaultPrayers.first()
    val countdown = countdownMin(nextPrayer)
    val countH = countdown / 60; val countM = countdown % 60

    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "islamic")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("أوقات الصلاة 🕌", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    // Next Prayer Banner
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF047857))))
                            .padding(20.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("الصلاة القادمة", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(.8f))
                            Spacer(Modifier.height(4.dp))
                            Text(nextPrayer.emoji + " " + nextPrayer.nameAr, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(String.format("%02d:%02d", nextPrayer.hour, nextPrayer.minute), style = MaterialTheme.typography.titleLarge, color = Color.White.copy(.9f))
                            Spacer(Modifier.height(8.dp))
                            Text("متبقي: ${countH}س ${countM}د", style = MaterialTheme.typography.titleMedium, color = Color(0xFFBBF7D0), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item {
                    Text("جميع الصلوات - القاهرة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    Text("*(أوقات تقريبية للقاهرة - يُنصح بالتحقق من التطبيقات الرسمية)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(cairoDefaultPrayers) { prayer ->
                    val isNext = prayer == nextPrayer
                    val isNow = prayerTotalMin(prayer) <= nowTotalMin && (cairoDefaultPrayers.indexOf(prayer) == cairoDefaultPrayers.indexOfLast { prayerTotalMin(it) <= nowTotalMin })
                    Card(
                        Modifier.fillMaxWidth(),
                        RoundedCornerShape(16.dp),
                        CardDefaults.cardColors(
                            containerColor = when {
                                isNext -> MaterialTheme.colorScheme.primaryContainer
                                isNow -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        CardDefaults.cardElevation(if (isNext) 4.dp else 1.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(prayer.emoji, fontSize = 28.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(prayer.nameAr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(prayer.nameEn, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text(String.format("%02d:%02d", prayer.hour, prayer.minute), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
