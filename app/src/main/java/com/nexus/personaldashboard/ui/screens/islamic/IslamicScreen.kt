package com.nexus.personaldashboard.ui.screens.islamic

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
import com.nexus.personaldashboard.ui.components.SectionBackground
import com.nexus.personaldashboard.ui.navigation.NavRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamicScreen(onNavigate: (NavRoute) -> Unit, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "islamic")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("إسلامي 🕌", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                IslamicCard("🕌", "أوقات الصلاة", "Prayer Times", listOf(Color(0xFF059669), Color(0xFF047857))) { onNavigate(NavRoute.PRAYER_TIMES) }
                IslamicCard("📖", "القرآن الكريم", "Holy Quran", listOf(Color(0xFF0891B2), Color(0xFF0E7490))) { onNavigate(NavRoute.QURAN) }
                IslamicCard("✨", "الأذكار", "Daily Azkar", listOf(Color(0xFF7C3AED), Color(0xFF6D28D9))) { onNavigate(NavRoute.AZKAR) }
            }
        }
    }
}

@Composable
fun IslamicCard(emoji: String, titleAr: String, titleEn: String, gradient: List<Color>, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(gradient))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 40.sp)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(titleAr, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Text(titleEn, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(.8f))
            }
        }
    }
}
