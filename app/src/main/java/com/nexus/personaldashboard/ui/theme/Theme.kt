package com.nexus.personaldashboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NexusPurpleLight,
    onPrimary = BackgroundDark,
    primaryContainer = Color(0xFF3B1A6E),
    onPrimaryContainer = NexusPurpleLight,
    secondary = NexusBlue,
    tertiary = NexusTeal,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = GlassBorderDark,
    error = Error
)

private val LightColorScheme = lightColorScheme(
    primary = NexusPurple,
    onPrimary = SurfaceLight,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = NexusPurpleDark,
    secondary = NexusBlue,
    tertiary = NexusTeal,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFE5E7EB),
    error = Error
)

@Composable
fun NexusTheme(
    themeMode: String = "SYSTEM",
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NexusTypography,
        shapes = NexusShapes,
        content = content
    )
}
