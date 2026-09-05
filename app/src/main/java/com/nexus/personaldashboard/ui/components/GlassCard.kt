package com.nexus.personaldashboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexus.personaldashboard.ui.theme.NexusShapes

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = NexusShapes.large,
    elevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    )
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}
