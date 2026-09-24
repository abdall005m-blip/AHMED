package com.nexus.personaldashboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nexus.personaldashboard.ui.screens.chat.PrivateChatScreen
import com.nexus.personaldashboard.ui.screens.coins.CoinStoreScreen
import com.nexus.personaldashboard.ui.screens.entertainment.EntertainmentScreen
import com.nexus.personaldashboard.ui.screens.games.*
import com.nexus.personaldashboard.ui.screens.home.HomeScreen
import com.nexus.personaldashboard.ui.screens.islamic.*
import com.nexus.personaldashboard.ui.screens.mood.MoodScreen
import com.nexus.personaldashboard.ui.screens.settings.SettingsScreen
import com.nexus.personaldashboard.ui.screens.notifications.NotificationsScreen
import com.nexus.personaldashboard.ui.screens.tasks.TasksScreen
import com.nexus.personaldashboard.ui.screens.schedule.ScheduleScreen
import com.nexus.personaldashboard.ui.screens.aihub.AIHubScreen
import com.nexus.personaldashboard.ui.screens.specialdays.SpecialDaysScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    fun navigate(route: NavRoute) = navController.navigate(route.name)
    fun back() = navController.popBackStack()

    NavHost(navController = navController, startDestination = NavRoute.HOME.name) {
        composable(NavRoute.HOME.name) {
            HomeScreen(onNavigate = { navigate(it) })
        }
        composable(NavRoute.ENTERTAINMENT.name) {
            EntertainmentScreen(onNavigate = { navigate(it) }, onBack = { back() })
        }
        composable(NavRoute.GAMES.name) {
            GamesScreen(onNavigate = { navigate(it) }, onBack = { back() })
        }
        composable(NavRoute.GAME_XO.name) {
            XOGameScreen(onBack = { back() })
        }
        composable(NavRoute.GAME_RACING.name) {
            CarRacingScreen(onBack = { back() })
        }
        composable(NavRoute.GAME_CONNECT_FOUR.name) {
            ConnectFourScreen(onBack = { back() })
        }
        composable(NavRoute.GAME_QUESTIONS.name) {
            QuestionsGameScreen(onBack = { back() })
        }
        composable(NavRoute.PRIVATE_CHAT.name) {
            PrivateChatScreen(onBack = { back() })
        }
        composable(NavRoute.ISLAMIC.name) {
            IslamicScreen(onNavigate = { navigate(it) }, onBack = { back() })
        }
        composable(NavRoute.PRAYER_TIMES.name) {
            PrayerTimesScreen(onBack = { back() })
        }
        composable(NavRoute.QURAN.name) {
            QuranScreen(onBack = { back() })
        }
        composable(NavRoute.AZKAR.name) {
            AzkarScreen(onBack = { back() })
        }
        composable(NavRoute.MOOD.name) {
            MoodScreen(onBack = { back() })
        }
        composable(NavRoute.COIN_STORE.name) {
            CoinStoreScreen(onBack = { back() })
        }
        composable(NavRoute.SETTINGS.name) {
            SettingsScreen(onBack = { back() })
        }
        composable(NavRoute.SPECIAL_DAYS.name) {
            SpecialDaysScreen(onBack = { back() })
        }
        composable(NavRoute.NOTIFICATIONS.name) {
            NotificationsScreen(onBack = { back() })
        }
        composable(NavRoute.TASKS.name) {
            TasksScreen(onBack = { back() })
        }
        composable(NavRoute.SCHEDULE.name) {
            ScheduleScreen(onBack = { back() })
        }
        composable(NavRoute.AI_HUB.name) {
            AIHubScreen(onBack = { back() })
        }
    }
}
