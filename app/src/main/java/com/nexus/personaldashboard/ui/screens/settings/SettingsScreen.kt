package com.nexus.personaldashboard.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.personaldashboard.ui.components.SectionBackground

data class AppThemeOption(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val emoji: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val gradient: List<Color>
)

val builtInThemes = listOf(
    AppThemeOption(
        "lavender", "حلم اللافندر", "Lavender Dream", "💜",
        Color(0xFF7C3AED), Color(0xFFA78BFA),
        listOf(Color(0xFF7C3AED), Color(0xFFC084FC))
    ),
    AppThemeOption(
        "cyber", "سايبر نايت", "Midnight Cyber", "🌌",
        Color(0xFF3B82F6), Color(0xFF06B6D4),
        listOf(Color(0xFF1E1B4B), Color(0xFF3B82F6))
    ),
    AppThemeOption(
        "rose", "وردي رومانسي", "Rose Romance", "🌸",
        Color(0xFFEC4899), Color(0xFFF43F5E),
        listOf(Color(0xFFBE185D), Color(0xFFF472B6))
    ),
    AppThemeOption(
        "emerald", "واحة الزمرد", "Emerald Oasis", "🌿",
        Color(0xFF059669), Color(0xFF10B981),
        listOf(Color(0xFF065F46), Color(0xFF34D399))
    ),
    AppThemeOption(
        "sunset", "شروق الشمس", "Sunset Glow", "🌅",
        Color(0xFFF59E0B), Color(0xFFEF4444),
        listOf(Color(0xFFD97706), Color(0xFFF87171))
    ),
    AppThemeOption(
        "ocean", "نسيم المحيط", "Ocean Breeze", "🌊",
        Color(0xFF0284C7), Color(0xFF38BDF8),
        listOf(Color(0xFF0369A1), Color(0xFF7DD3FC))
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var selectedThemeId by remember { mutableStateOf("lavender") }
    var themeMode by remember { mutableStateOf(1) } // 0=Light, 1=System, 2=Dark
    var selectedLang by remember { mutableStateOf("ar") }
    var notifEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(false) }
    var showOnline by remember { mutableStateOf(true) }
    var showLastSeen by remember { mutableStateOf(true) }
    var showTyping by remember { mutableStateOf(true) }
    var readReceipts by remember { mutableStateOf(true) }

    val accentColors = listOf(
        Color(0xFF7C3AED), Color(0xFF2563EB), Color(0xFF059669),
        Color(0xFFEC4899), Color(0xFFF59E0B), Color(0xFFEF4444)
    )
    var selectedAccent by remember { mutableStateOf(0) }

    Box(Modifier.fillMaxSize()) {
        SectionBackground(section = "settings")
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            TopAppBar(
                title = { Text("الإعدادات والثيمات ⚙️", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Dedicated Themes Section
                item { SectionHeader("✨ ثيمات التطبيق الجاهزة  •  Themes") }
                item {
                    Text(
                        "اختار الثيم العام للتطبيق بألوان وتدرجات حصرية:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(builtInThemes, key = { it.id }) { theme ->
                            val isSelected = selectedThemeId == theme.id
                            Card(
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedThemeId = theme.id },
                                shape = RoundedCornerShape(20.dp),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Gradient Preview Box
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Brush.linearGradient(theme.gradient)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(theme.emoji, fontSize = 22.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            theme.nameAr,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            theme.nameEn,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isSelected) {
                                        Box(
                                            Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primary)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text("مُفعّل ✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Appearance (Dark / Light / Accent)
                item { SectionHeader("🎨 تخصيص المظهر  •  Appearance") }
                item {
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("وضع العرض", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                                listOf("☀️ فاتح", "📱 تلقائي", "🌙 داكن").forEachIndexed { i, label ->
                                    SegmentedButton(themeMode == i, { themeMode = i }, RoundedCornerShape(12.dp)) { Text(label, fontSize = 12.sp) }
                                }
                            }
                            HorizontalDivider()
                            Text("اللون الأساسي (Accent Color)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                accentColors.forEachIndexed { i, color ->
                                    Box(
                                        Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .clickable { selectedAccent = i },
                                        Alignment.Center
                                    ) {
                                        if (selectedAccent == i) {
                                            Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Language
                item { SectionHeader("🌍 اللغة  •  Language") }
                item {
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp)) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("ar" to "🇸🇦 العربية", "en" to "🇬🇧 English").forEach { (code, label) ->
                                FilterChip(selectedLang == code, { selectedLang = code }, { Text(label) }, Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Permissions
                item { SectionHeader("🔐 الأذونات  •  Permissions") }
                item {
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp)) {
                        Column(Modifier.fillMaxWidth().padding(4.dp)) {
                            SettingsToggle("الإشعارات", "استقبال الإشعارات والمهام", Icons.Rounded.Notifications, notifEnabled) { notifEnabled = it }
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            SettingsToggle("الموقع", "لحساب أوقات الصلاة بدقة", Icons.Rounded.LocationOn, locationEnabled) { locationEnabled = it }
                        }
                    }
                }

                // Chat Privacy
                item { SectionHeader("🔒 خصوصية الشات  •  Chat Privacy") }
                item {
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp)) {
                        Column(Modifier.fillMaxWidth().padding(4.dp)) {
                            SettingsToggle("اظهر حالة الاتصال", "يشوف الطرف الآخر لو انت اونلاين", Icons.Rounded.Visibility, showOnline) { showOnline = it }
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            SettingsToggle("اظهر آخر ظهور", "يشوف امتى آخر مرة فتحت التطبيق", Icons.Rounded.AccessTime, showLastSeen) { showLastSeen = it }
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            SettingsToggle("اظهر 'بيكتب...'", "يشوف لما بتكتب رسالة في الشات", Icons.Rounded.Edit, showTyping) { showTyping = it }
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            SettingsToggle("علامات القراءة", "إظهار لما الرسالة تتقرأ", Icons.Rounded.DoneAll, readReceipts) { readReceipts = it }
                        }
                    }
                }

                // About
                item { SectionHeader("ℹ️ عن التطبيق") }
                item {
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🤖", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Nexus Personal Dashboard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("الإصدار 3.0 ✨", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(4.dp))
                            Text("صُنع بكل ❤️ لأحمد ورودي", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsToggle(title: String, subtitle: String, icon: ImageVector, checked: Boolean, onChanged: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(24.dp), MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onChanged)
    }
}
