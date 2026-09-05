package com.nexus.personaldashboard.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nexus.personaldashboard.data.preferences.UserPreferencesRepository
import com.nexus.personaldashboard.ui.components.NexusBottomNavigationBar
import com.nexus.personaldashboard.ui.screens.aihub.AIHubScreen
import com.nexus.personaldashboard.ui.screens.aihub.AddEditAIAppScreen
import com.nexus.personaldashboard.ui.screens.entertainment.EntertainmentScreen
import com.nexus.personaldashboard.ui.screens.home.HomeScreen
import com.nexus.personaldashboard.ui.screens.notifications.NotificationsScreen
import com.nexus.personaldashboard.ui.screens.onboarding.OnboardingScreen
import com.nexus.personaldashboard.ui.screens.schedule.AddEditScheduleScreen
import com.nexus.personaldashboard.ui.screens.schedule.ScheduleScreen
import com.nexus.personaldashboard.ui.screens.search.SearchScreen
import com.nexus.personaldashboard.ui.screens.settings.SettingsScreen
import com.nexus.personaldashboard.ui.screens.tasks.AddEditTaskScreen
import com.nexus.personaldashboard.ui.screens.tasks.TasksScreen

val bottomNavRoutes = setOf(
    NavRoute.HOME,
    NavRoute.NOTIFICATIONS,
    NavRoute.TASKS,
    NavRoute.ENTERTAINMENT,
    NavRoute.AI_HUB,
    NavRoute.SCHEDULE
)

@Composable
fun AppNavigation(
    prefsRepo: UserPreferencesRepository
) {
    val isOnboardingDone by prefsRepo.isOnboardingDone.collectAsState(initial = true)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteString = navBackStackEntry?.destination?.route?.substringBefore("/")
    val currentRoute = try {
        NavRoute.valueOf(currentRouteString ?: NavRoute.HOME.name)
    } catch (e: Exception) {
        NavRoute.HOME
    }

    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NexusBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (!isOnboardingDone) NavRoute.ONBOARDING.name else NavRoute.HOME.name,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() + slideInHorizontally(initialOffsetX = { 40 }) },
            exitTransition = { fadeOut() }
        ) {
            composable(NavRoute.ONBOARDING.name) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(NavRoute.HOME.name) {
                            popUpTo(NavRoute.ONBOARDING.name) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoute.HOME.name) {
                HomeScreen(
                    onNavigate = { route ->
                        navController.navigate(route.name)
                    }
                )
            }

            composable(NavRoute.NOTIFICATIONS.name) {
                NotificationsScreen()
            }

            composable(NavRoute.AI_HUB.name) {
                AIHubScreen(
                    onAddApp = { navController.navigate(NavRoute.ADD_AI_APP.name) },
                    onEditApp = { appId -> navController.navigate("${NavRoute.EDIT_AI_APP.name}/$appId") }
                )
            }

            composable(NavRoute.TASKS.name) {
                TasksScreen(
                    onAddTask = { navController.navigate(NavRoute.ADD_TASK.name) },
                    onEditTask = { taskId -> navController.navigate("${NavRoute.EDIT_TASK.name}/$taskId") }
                )
            }

            composable(NavRoute.SCHEDULE.name) {
                ScheduleScreen(
                    onAdd = { navController.navigate(NavRoute.ADD_SCHEDULE.name) },
                    onEdit = { scheduleId -> navController.navigate("${NavRoute.EDIT_SCHEDULE.name}/$scheduleId") }
                )
            }

            composable(NavRoute.ENTERTAINMENT.name) {
                EntertainmentScreen(onBack = { navController.popBackStack() })
            }

            composable(NavRoute.SETTINGS.name) {
                SettingsScreen()
            }

            composable(NavRoute.SEARCH.name) {
                SearchScreen(onBack = { navController.popBackStack() })
            }

            composable(NavRoute.ADD_TASK.name) {
                AddEditTaskScreen(onBack = { navController.popBackStack() })
            }

            composable("${NavRoute.EDIT_TASK.name}/{taskId}") { backStack ->
                val id = backStack.arguments?.getString("taskId")?.toLongOrNull() ?: 0L
                AddEditTaskScreen(taskId = id, onBack = { navController.popBackStack() })
            }

            composable(NavRoute.ADD_SCHEDULE.name) {
                AddEditScheduleScreen(onBack = { navController.popBackStack() })
            }

            composable("${NavRoute.EDIT_SCHEDULE.name}/{scheduleId}") { backStack ->
                val id = backStack.arguments?.getString("scheduleId")?.toLongOrNull() ?: 0L
                AddEditScheduleScreen(scheduleId = id, onBack = { navController.popBackStack() })
            }

            composable(NavRoute.ADD_AI_APP.name) {
                AddEditAIAppScreen(onBack = { navController.popBackStack() })
            }

            composable("${NavRoute.EDIT_AI_APP.name}/{appId}") { backStack ->
                val id = backStack.arguments?.getString("appId")?.toLongOrNull() ?: 0L
                AddEditAIAppScreen(appId = id, onBack = { navController.popBackStack() })
            }
        }
    }
}
