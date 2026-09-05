package com.nexus.personaldashboard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nexus.personaldashboard.ui.navigation.NavRoute

data class BottomNavItem(
    val route: NavRoute,
    val icon: ImageVector,
    val label: String
)

@Composable
fun NexusBottomNavigationBar(
    currentRoute: NavRoute,
    onNavigate: (NavRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(NavRoute.HOME, Icons.Rounded.Home, "Home"),
        BottomNavItem(NavRoute.NOTIFICATIONS, Icons.Rounded.Notifications, "Notifications"),
        BottomNavItem(NavRoute.AI_HUB, Icons.Rounded.Psychology, "AI Hub"),
        BottomNavItem(NavRoute.TASKS, Icons.Rounded.CheckCircle, "Tasks"),
        BottomNavItem(NavRoute.SCHEDULE, Icons.Rounded.CalendarMonth, "Schedule")
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 4.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val iconColor by animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "icon_color"
            )

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconColor
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = iconColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
