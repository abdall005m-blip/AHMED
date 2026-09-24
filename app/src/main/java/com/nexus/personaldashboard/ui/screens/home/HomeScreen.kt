package com.nexus.personaldashboard.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.personaldashboard.ui.components.SectionBackground
import com.nexus.personaldashboard.ui.components.MascotAssistant
import com.nexus.personaldashboard.ui.components.SpecialDayOverlay
import com.nexus.personaldashboard.ui.components.ChangeVibeButton
import com.nexus.personaldashboard.ui.navigation.NavRoute
import com.nexus.personaldashboard.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class HomeCard(
    val route: NavRoute,
    val title: String,
    val titleAr: String,
    val emoji: String,
    val description: String,
    val accentColor: Color,
    val gradientColors: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (NavRoute) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val greeting = getGreeting()
    val dateStr = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())
    var showMascotChat by remember { mutableStateOf(false) }

    val cards = listOf(
        HomeCard(NavRoute.ENTERTAINMENT, "Entertainment", "ترفيه", "🎮", "Games, Chat & Fun", EntertainmentAccent,
            listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))),
        HomeCard(NavRoute.PRIVATE_CHAT, "Private Chat", "الشات الخاص", "💬", "Your secret space", ChatAccent,
            listOf(Color(0xFFEC4899), Color(0xFFBE185D))),
        HomeCard(NavRoute.ISLAMIC, "Islamic", "إسلامي", "🕌", "Quran, Azkar & Prayer", IslamicAccent,
            listOf(Color(0xFF059669), Color(0xFF047857))),
        HomeCard(NavRoute.MOOD, "Mood", "مزاجي", "💖", "How are you feeling?", MoodAccent,
            listOf(Color(0xFFF59E0B), Color(0xFFD97706))),
        HomeCard(NavRoute.TASKS, "Tasks", "المهام", "✅", "Your to-do list", TasksAccent,
            listOf(Color(0xFF10B981), Color(0xFF059669))),
        HomeCard(NavRoute.SCHEDULE, "Schedule", "جدولي", "📅", "Your weekly plan", ScheduleAccent,
            listOf(Color(0xFF3B82F6), Color(0xFF2563EB))),
        HomeCard(NavRoute.NOTIFICATIONS, "Notifications", "الإشعارات", "🔔", "Stay updated", NotificationsAccent,
            listOf(Color(0xFFF97316), Color(0xFFEA580C))),
        HomeCard(NavRoute.AI_HUB, "AI Hub", "مركز الذكاء", "🤖", "Your AI tools", AIAccent,
            listOf(Color(0xFF06B6D4), Color(0xFF0891B2))),
        HomeCard(NavRoute.SPECIAL_DAYS, "Special Days", "الأيام المميزة", "🎉", "عيد ومناسبات خاصة", ScheduleAccent,
            listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))),
        HomeCard(NavRoute.SETTINGS, "Settings", "الإعدادات", "⚙️", "Customize your app", SettingsAccent,
            listOf(Color(0xFF6B7280), Color(0xFF4B5563)))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Section-specific background
        SectionBackground(section = "home")

        // Special day overlay (sheep for Eid, etc.)
        SpecialDayOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            HomeHeader(
                greeting = greeting,
                dateStr = dateStr,
                coinsBalance = state.coinsBalance,
                onMascotClick = { showMascotChat = true },
                onSettingsClick = { onNavigate(NavRoute.SETTINGS) },
                onCoinStoreClick = { onNavigate(NavRoute.COIN_STORE) }
            )

            // Change the Vibe button
            ChangeVibeButton(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

            // Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(cards) { card ->
                    HomeFeatureCard(
                        card = card,
                        onClick = { onNavigate(card.route) }
                    )
                }
            }
        }

        // Mascot Assistant overlay
        if (showMascotChat) {
            MascotAssistant(
                onNavigate = onNavigate,
                onDismiss = { showMascotChat = false }
            )
        }
    }
}

@Composable
fun HomeHeader(
    greeting: String,
    dateStr: String,
    coinsBalance: Int,
    onMascotClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCoinStoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mascot avatar (replaces search)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Purple60, Purple40)
                    )
                )
                .clickable { onMascotClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("🤖", fontSize = 24.sp)
        }

        // Title + date
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Nexus ✨",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Coins + Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Coins badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { onCoinStoreClick() }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🪙", fontSize = 14.sp)
                    Text(
                        text = coinsBalance.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CoinsAccent
                    )
                }
            }

            // Settings
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HomeFeatureCard(
    card: HomeCard,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(scale)
            .clickable {
                pressed = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = card.gradientColors.map { it.copy(alpha = 0.9f) }
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = card.emoji,
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = card.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1
                    )
                }
            }
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }
}

fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning ☀️"
        hour < 17 -> "Good Afternoon 🌤️"
        hour < 21 -> "Good Evening 🌅"
        else -> "Good Night 🌙"
    }
}
