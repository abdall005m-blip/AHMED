package com.nexus.personaldashboard.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.personaldashboard.ui.components.SectionBackground

data class ScheduleItem(val day: String, val dayAr: String, val items: List<String>)

val weekSchedule = listOf(
    ScheduleItem("Monday", "الاثنين", listOf("9:00 اجتماع العمل", "14:00 وجبة الغداء", "18:00 الجيم")),
    ScheduleItem("Tuesday", "الثلاثاء", listOf("10:00 محاضرة", "16:00 مراجعة", "20:00 وقت حر")),
    ScheduleItem("Wednesday", "الأربعاء", listOf("9:00 عمل", "15:00 قهوة مع صديق")),
    ScheduleItem("Thursday", "الخميس", listOf("10:00 اجتماع", "17:00 تسوق", "19:00 عيلة")),
    ScheduleItem("Friday", "الجمعة", listOf("12:00 صلاة الجمعة", "15:00 راحة", "20:00 خروج")),
    ScheduleItem("Saturday", "السبت", listOf("11:00 صحيان متأخر", "14:00 سينما", "19:00 عشاء برا")),
    ScheduleItem("Sunday", "الأحد", listOf("10:00 تنظيم البيت", "16:00 تحضير الأسبوع")),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(onBack: () -> Unit) {
    val dayIndex = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) - 2
    val todayIndex = dayIndex.coerceIn(0, 6)

    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "schedule")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("جدولي 📅", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text("الجدول الأسبوعي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(weekSchedule.size) { i ->
                    val schedule = weekSchedule[i]
                    val isToday = i == todayIndex
                    Card(
                        Modifier.fillMaxWidth(),
                        RoundedCornerShape(16.dp),
                        CardDefaults.cardColors(if (isToday) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                        CardDefaults.cardElevation(if (isToday) 4.dp else 1.dp)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(schedule.dayAr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                if (isToday) {
                                    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primary) {
                                        Text("اليوم", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            if (schedule.items.isEmpty()) {
                                Text("🎉 يوم إجازة!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                schedule.items.forEach { item ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                        Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                                        Text(item, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
