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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.personaldashboard.ui.components.SectionBackground
import java.util.Calendar

data class Zikr(val arabic: String, val count: Int, val category: String)

val morningAzkar = listOf(
    Zikr("أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ", 1, "morning"),
    Zikr("اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ", 1, "morning"),
    Zikr("لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ", 100, "morning"),
    Zikr("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", 100, "morning"),
    Zikr("أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ", 3, "morning"),
    Zikr("بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ", 3, "morning"),
    Zikr("رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا", 3, "morning"),
    Zikr("اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ", 1, "morning"),
)

val eveningAzkar = listOf(
    Zikr("أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ", 1, "evening"),
    Zikr("اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ", 1, "evening"),
    Zikr("لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ", 100, "evening"),
    Zikr("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ", 3, "evening"),
    Zikr("اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي", 3, "evening"),
    Zikr("اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْكُفْرِ، وَالْفَقْرِ، وَعَذَابِ الْقَبْرِ", 3, "evening"),
    Zikr("حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ، عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ", 7, "evening"),
    Zikr("اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ", 10, "evening"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarScreen(onBack: () -> Unit) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    var isMorning by remember { mutableStateOf(hour in 4..14) }
    val azkarList = if (isMorning) morningAzkar else eveningAzkar
    val counters = remember(isMorning) { mutableStateMapOf<Int, Int>() }

    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "islamic")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text(if (isMorning) "أذكار الصباح ☀️" else "أذكار المساء 🌙", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(isMorning, { isMorning = true }, { Text("☀️ الصباح") }, Modifier.weight(1f))
                FilterChip(!isMorning, { isMorning = false }, { Text("🌙 المساء") }, Modifier.weight(1f))
            }
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(azkarList.size) { i ->
                    val zikr = azkarList[i]
                    val count = counters.getOrDefault(i, 0)
                    val done = count >= zikr.count
                    Card(
                        Modifier.fillMaxWidth(),
                        RoundedCornerShape(16.dp),
                        CardDefaults.cardColors(if (done) Color(0xFF059669).copy(.1f) else MaterialTheme.colorScheme.surface),
                        CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(
                                text = zikr.arabic,
                                style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Rtl),
                                textAlign = TextAlign.Right,
                                lineHeight = 30.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${count} / ${zikr.count}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (done) Color(0xFF059669) else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                if (done) {
                                    Text("✅ تم", style = MaterialTheme.typography.labelLarge, color = Color(0xFF059669))
                                } else {
                                    Button(
                                        onClick = { counters[i] = (counters.getOrDefault(i, 0) + 1).coerceAtMost(zikr.count) },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                    ) { Text("سبّح") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
