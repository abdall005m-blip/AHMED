package com.nexus.personaldashboard.ui.screens.onboarding

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.personaldashboard.service.NotificationAccessHelper
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val gradient: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPageData(
            emoji = "🔔",
            title = "All your notifications\nin one place",
            subtitle = "Stay on top of everything without missing anything important",
            gradient = listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))
        ),
        OnboardingPageData(
            emoji = "📅",
            title = "Organize your tasks\nand schedule",
            subtitle = "Manage your daily tasks, create schedules, and get timely reminders",
            gradient = listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))
        ),
        OnboardingPageData(
            emoji = "🤖",
            title = "All your AI tools\nin one hub",
            subtitle = "Access ChatGPT, Gemini, Claude, and more with a single tap",
            gradient = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
        ),
        OnboardingPageData(
            emoji = "✨",
            title = "Stay in control\nof your day",
            subtitle = "Your personal command center — beautiful, fast, and private",
            gradient = listOf(Color(0xFF10B981), Color(0xFF3B82F6))
        )
    )

    val pagerState = rememberPagerState { pages.size + 1 }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            if (page < pages.size) {
                OnboardingPageView(page = pages[page])
            } else {
                NotificationPermissionPage(
                    context = context,
                    onContinue = {
                        scope.launch {
                            viewModel.setOnboardingDone()
                            onFinish()
                        }
                    }
                )
            }
        }

        if (pagerState.currentPage < pages.size) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    repeat(pages.size + 1) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "dot_width"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) "Grant Notification Access" else "Next",
                        color = Color(0xFF7C3AED),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pages.size)
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Skip", color = Color.White.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageView(page: OnboardingPageData) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(page.gradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = page.emoji, fontSize = 80.sp)
            Spacer(Modifier.height(32.dp))
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NotificationPermissionPage(
    context: Context,
    onContinue: () -> Unit
) {
    var isEnabled by remember {
        mutableStateOf(NotificationAccessHelper.isNotificationAccessEnabled(context))
    }

    LaunchedEffect(Unit) {
        isEnabled = NotificationAccessHelper.isNotificationAccessEnabled(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF4C1D95), Color(0xFF1E1B4B))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🔐", fontSize = 72.sp)
            Spacer(Modifier.height(24.dp))
            Text(
                text = "السماح بالوصول إلى الإشعارات\nNotification Access",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "اسمح للتطبيق بالوصول إلى إشعاراتك حتى نتمكن من تنظيمها وعرضها في مكان واحد.\nيتم حفظ جميع الإشعارات محلياً بأمان على جهازك.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEnabled) Color(0x3310B981) else Color(0x33F43F5E)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isEnabled) "Notification Access ● Enabled" else "Notification Access ○ Disabled",
                        color = if (isEnabled) Color(0xFF10B981) else Color(0xFFF43F5E),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (!isEnabled) {
                Button(
                    onClick = {
                        NotificationAccessHelper.openNotificationAccessSettings(context)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        "Open Settings / فتح الإعدادات",
                        color = Color(0xFF7C3AED),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Text("Continue / متابعة", color = Color.White.copy(alpha = 0.8f))
                }
            } else {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("ابدأ الآن 🎉 Get Started", color = Color(0xFF7C3AED), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
