package com.nexus.personaldashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nexus.personaldashboard.domain.model.NotificationPriority
import com.nexus.personaldashboard.domain.model.TaskPriority
import com.nexus.personaldashboard.ui.theme.PriorityHigh
import com.nexus.personaldashboard.ui.theme.PriorityLow
import com.nexus.personaldashboard.ui.theme.PriorityMedium

@Composable
fun TaskPriorityBadge(priority: TaskPriority, modifier: Modifier = Modifier) {
    val (color, label) = when (priority) {
        TaskPriority.HIGH -> PriorityHigh to "High"
        TaskPriority.MEDIUM -> PriorityMedium to "Medium"
        TaskPriority.LOW -> PriorityLow to "Low"
    }
    PriorityBadge(color = color, label = label, modifier = modifier)
}

@Composable
fun NotifPriorityBadge(priority: NotificationPriority, modifier: Modifier = Modifier) {
    val (color, label) = when (priority) {
        NotificationPriority.HIGH -> PriorityHigh to "High"
        NotificationPriority.MEDIUM -> PriorityMedium to "Medium"
        NotificationPriority.LOW -> PriorityLow to "Low"
    }
    PriorityBadge(color = color, label = label, modifier = modifier)
}

@Composable
private fun PriorityBadge(color: Color, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
