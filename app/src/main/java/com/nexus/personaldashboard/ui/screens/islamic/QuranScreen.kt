package com.nexus.personaldashboard.ui.screens.islamic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
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

data class Surah(val number: Int, val nameAr: String, val nameEn: String, val verses: Int)

val allSurahs = listOf(
    Surah(1, "الفاتحة", "Al-Fatiha", 7), Surah(2, "البقرة", "Al-Baqarah", 286), Surah(3, "آل عمران", "Aali Imran", 200),
    Surah(4, "النساء", "An-Nisa", 176), Surah(5, "المائدة", "Al-Ma'idah", 120), Surah(6, "الأنعام", "Al-An'am", 165),
    Surah(7, "الأعراف", "Al-A'raf", 206), Surah(8, "الأنفال", "Al-Anfal", 75), Surah(9, "التوبة", "At-Tawbah", 129),
    Surah(10, "يونس", "Yunus", 109), Surah(11, "هود", "Hud", 123), Surah(12, "يوسف", "Yusuf", 111),
    Surah(13, "الرعد", "Ar-Ra'd", 43), Surah(14, "إبراهيم", "Ibrahim", 52), Surah(15, "الحجر", "Al-Hijr", 99),
    Surah(16, "النحل", "An-Nahl", 128), Surah(17, "الإسراء", "Al-Isra", 111), Surah(18, "الكهف", "Al-Kahf", 110),
    Surah(19, "مريم", "Maryam", 98), Surah(20, "طه", "Ta-Ha", 135), Surah(21, "الأنبياء", "Al-Anbiya", 112),
    Surah(22, "الحج", "Al-Hajj", 78), Surah(23, "المؤمنون", "Al-Mu'minun", 118), Surah(24, "النور", "An-Nur", 64),
    Surah(25, "الفرقان", "Al-Furqan", 77), Surah(26, "الشعراء", "Ash-Shu'ara", 227), Surah(27, "النمل", "An-Naml", 93),
    Surah(28, "القصص", "Al-Qasas", 88), Surah(29, "العنكبوت", "Al-Ankabut", 69), Surah(30, "الروم", "Ar-Rum", 60),
    Surah(31, "لقمان", "Luqman", 34), Surah(32, "السجدة", "As-Sajdah", 30), Surah(33, "الأحزاب", "Al-Ahzab", 73),
    Surah(34, "سبأ", "Saba", 54), Surah(35, "فاطر", "Fatir", 45), Surah(36, "يس", "Ya-Sin", 83),
    Surah(37, "الصافات", "As-Saffat", 182), Surah(38, "ص", "Sad", 88), Surah(39, "الزمر", "Az-Zumar", 75),
    Surah(40, "غافر", "Ghafir", 85), Surah(100, "العاديات", "Al-Adiyat", 11), Surah(110, "النصر", "An-Nasr", 3),
    Surah(112, "الإخلاص", "Al-Ikhlas", 4), Surah(113, "الفلق", "Al-Falaq", 5), Surah(114, "الناس", "An-Nas", 6),
)

val fatihaVerses = listOf(
    "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
    "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
    "الرَّحْمَنِ الرَّحِيمِ",
    "مَالِكِ يَوْمِ الدِّينِ",
    "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
    "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
    "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSurah by remember { mutableStateOf<Surah?>(null) }

    val filtered = allSurahs.filter {
        searchQuery.isEmpty() || it.nameAr.contains(searchQuery) || it.nameEn.contains(searchQuery, true) || it.number.toString() == searchQuery
    }

    if (selectedSurah != null) {
        SurahDetailScreen(selectedSurah!!) { selectedSurah = null }
        return
    }

    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "islamic")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("القرآن الكريم 📖", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("ابحث عن سورة...") },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                shape = RoundedCornerShape(16.dp), singleLine = true
            )
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered.size) { i ->
                    val surah = filtered[i]
                    Card(Modifier.fillMaxWidth().clickable { selectedSurah = surah }, RoundedCornerShape(12.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface), CardDefaults.cardElevation(2.dp)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)), Alignment.Center) {
                                Text(surah.number.toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(surah.nameAr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(surah.nameEn, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("${surah.verses} آية", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(surah: Surah, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "islamic")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("${surah.nameAr} - ${surah.nameEn}", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (surah.number == 1) {
                    item {
                        Text("بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ", Modifier.fillMaxWidth().padding(bottom = 8.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    items(fatihaVerses.size) { i ->
                        Card(Modifier.fillMaxWidth(), RoundedCornerShape(12.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(32.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)), Alignment.Center) {
                                    Text((i + 1).toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(fatihaVerses[i], Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Rtl), textAlign = TextAlign.Right, lineHeight = 28.sp)
                            }
                        }
                    }
                } else {
                    item {
                        Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)) {
                            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📖", fontSize = 48.sp)
                                Spacer(Modifier.height(12.dp))
                                Text("سورة ${surah.nameAr}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                Text("${surah.verses} آية", style = MaterialTheme.typography.bodyLarge)
                                Spacer(Modifier.height(12.dp))
                                Text("النص الكامل للسورة متاح عبر التطبيقات الرسمية.\nيُنصح باستخدام تطبيق القرآن الكريم للتلاوة الكاملة.", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
