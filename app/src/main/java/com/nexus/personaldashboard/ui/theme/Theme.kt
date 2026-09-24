package com.nexus.personaldashboard.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NexusDarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple10,
    primaryContainer = Purple40,
    onPrimaryContainer = Purple90,
    secondary = Lavender60,
    onSecondary = Purple10,
    secondaryContainer = Lavender40,
    onSecondaryContainer = Lavender80,
    tertiary = Rose60,
    onTertiary = Color.White,
    tertiaryContainer = Rose40,
    onTertiaryContainer = Rose80,
    background = NeutralDark,
    onBackground = NeutralLight,
    surface = SurfaceDark,
    onSurface = NeutralLight,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Lavender80,
    outline = NeutralSoft,
    error = Rose60,
    onError = Color.White
)

private val NexusLightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Purple90,
    onPrimaryContainer = Purple20,
    secondary = Lavender40,
    onSecondary = Color.White,
    secondaryContainer = Lavender80,
    onSecondaryContainer = Purple20,
    tertiary = Rose40,
    onTertiary = Color.White,
    tertiaryContainer = Rose80,
    onTertiaryContainer = Rose40,
    background = NeutralLight,
    onBackground = NeutralDark,
    surface = SurfaceLight,
    onSurface = NeutralDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Purple40,
    outline = Lavender80,
    error = Rose40,
    onError = Color.White
)

@Composable
fun NexusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NexusDarkColorScheme else NexusLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NexusTypography,
        content = content
    )
}
