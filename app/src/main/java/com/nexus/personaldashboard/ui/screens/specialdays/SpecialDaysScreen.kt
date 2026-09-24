package com.nexus.personaldashboard.ui.screens.specialdays

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.nexus.personaldashboard.ui.components.*

data class SpecialEventItem(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val dateStr: String,
    val emoji: String,
    val type: SpecialDayType,
    val description: String,
    val gradient: List<Color>
)

val allSpecialEvents = listOf(
    SpecialEventItem(
        "eid_adha", "عيد الأضحى المبارك", "Eid Al-Adha", "10 ذو الحجة",
        "🐑", SpecialDayType.EID_AL_ADHA,
        "أنيميشن خروف العيد الظريف يمشي على الشاشة مع النجوم الاحتفالية",
        listOf(Color(0xFF059669), Color(0xFF10B981))
    ),
    SpecialEventItem(
        "ramadan", "شهر رمضان المبارك", "Holy Ramadan", "1 رمضان",
        "🌙", SpecialDayType.RAMADAN,
        "هلال رمضان الذهبي مع نجوم مضيئة متلألئة وزينة روحانية",
        listOf(Color(0xFFD97706), Color(0xFFF59E0B))
    ),
    SpecialEventItem(
        "mothers_day", "عيد الأم", "Mother's Day", "21 مارس",
        "💖", SpecialDayType.MOTHERS_DAY,
        "قلوب متطايرة ناعمة وتدرجات وردية رومانسية",
        listOf(Color(0xFFBE185D), Color(0xFFEC4899))
    ),
    SpecialEventItem(
        "national_day", "اليوم الوطني", "National Day", "23 يوليو",
        "🎆", SpecialDayType.NATIONAL,
        "ألعاب نارية احتفالية وشعارات مبهجة",
        listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))
    ),
    SpecialEventItem(
        "anniversary", "ذكرى مميزة / عيد ميلاد", "Special Anniversary", "تاريخ مخصص",
        "🎉", SpecialDayType.OTHER,
        "قصاصات احتفالية وبالونات ملونة مخصصة لأحمد ورودي",
        listOf(Color(0xFF7C3AED), Color(0xFFA855F7))
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialDaysScreen(onBack: () -> Unit) {
    var activePreviewType by remember { mutableStateOf<SpecialDayType?>(null) }
    var selectedEventId by remember { mutableStateOf<String?>(null) }
    var showAddCustomDialog by remember { mutableStateOf(false) }
    var customName by remember { mutableStateOf("") }
    var customDate by remember { mutableStateOf("") }

    val eventsList = remember { mutableStateListOf(*allSpecialEvents.toTypedArray()) }

    if (showAddCustomDialog) {
        AlertDialog(
            onDismissRequest = { showAddCustomDialog = false },
            title = { Text("إضافة مناسبة خاصة جديدة 🎂", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("اسم المناسبة (مثال: عيد ميلاد رودي)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customDate,
                        onValueChange = { customDate = it },
                        label = { Text("التاريخ (مثال: 15 أكتوبر)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customName.isNotBlank()) {
                            eventsList.add(
                                SpecialEventItem(
                                    id = "custom_${System.currentTimeMillis()}",
                                    nameAr = customName,
                                    nameEn = "Custom Event",
                                    dateStr = customDate.ifBlank { "قريباً" },
                                    emoji = "🎁",
                                    type = SpecialDayType.OTHER,
                                    description = "ثيم واحتفال خاص بمناسبتك المميزة",
                                    gradient = listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))
                                )
                            )
                            customName = ""
                            customDate = ""
                            showAddCustomDialog = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("إضافة") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomDialog = false }) { Text("إلغاء") }
            }
        )
    }

    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "home")

        // Live preview overlay if active
        if (activePreviewType != null) {
            when (activePreviewType) {
                SpecialDayType.EID_AL_ADHA -> EidSheepAnimation()
                SpecialDayType.RAMADAN -> RamadanDecoration()
                SpecialDayType.MOTHERS_DAY -> MothersDay()
                else -> RamadanDecoration()
            }
        }

        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("ثيمات الأيام المميزة 🎉", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { showAddCustomDialog = true }) {
                        Icon(Icons.Rounded.AddCircle, "إضافة مناسبة", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            // Info banner
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 28.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("التطبيق بيتعرف تلقائياً على المناسبات", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("تقدر تدوس 'معاينة الثيم' لتجربة الأنيميشن فوراً على الشاشة!", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (activePreviewType != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("جاري معاينة الثيم الحي على الشاشة الآن ✨", style = MaterialTheme.typography.labelMedium)
                        TextButton(onClick = { activePreviewType = null }) {
                            Text("إيقاف المعاينة ✕", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(eventsList, key = { it.id }) { event ->
                    val isSelected = selectedEventId == event.id
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(event.gradient)),
                                    Alignment.Center
                                ) {
                                    Text(event.emoji, fontSize = 24.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(event.nameAr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(event.nameEn + "  •  " + event.dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                event.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        activePreviewType = if (activePreviewType == event.type) null else event.type
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (activePreviewType == event.type) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(if (activePreviewType == event.type) "إيقاف المعاينة ✕" else "معاينة الثيم والأنيميشن ✨")
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}
