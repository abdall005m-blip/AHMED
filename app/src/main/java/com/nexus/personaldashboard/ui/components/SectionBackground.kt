package com.nexus.personaldashboard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SectionBackground(section: String, modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val (bgColors, decorColor) = getSectionColors(section, isDark)
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val animVal by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "anim"
    )
    Box(modifier = modifier.fillMaxSize().background(Brush.verticalGradient(bgColors))) {
        Canvas(Modifier.fillMaxSize()) { drawDecorations(section, animVal, decorColor) }
    }
}

fun DrawScope.drawDecorations(section: String, anim: Float, color: Color) {
    val w = size.width; val h = size.height
    when (section) {
        "home" -> {
            for (i in 0..5) {
                val angle = anim * 2 * PI.toFloat() + i * PI.toFloat() / 3
                drawCircle(color.copy(0.06f), 60f + i * 20f, Offset(w * .5f + cos(angle) * w * .3f, h * .3f + sin(angle * .7f) * h * .15f))
            }
            listOf(Offset(w*.1f,h*.1f),Offset(w*.9f,h*.15f),Offset(w*.2f,h*.8f),Offset(w*.85f,h*.7f),Offset(w*.5f,h*.05f)).forEach {
                val p = (sin(anim * 2 * PI.toFloat() + it.x) + 1) / 2
                drawCircle(color.copy(.1f + p * .05f), 4f + p * 3f, it)
            }
        }
        "games","entertainment" -> (0..7).forEach { i ->
            drawCircle(color.copy(.08f), 8f + i * 5f, Offset((anim * (1f + i * .1f) % 1f) * w, h * (.1f + i * .1f)))
        }
        "islamic" -> (0..12).forEach { i ->
            val a = i.toFloat() / 12f * 2 * PI.toFloat()
            val p = (sin(anim * 2 * PI.toFloat() + a) + 1) / 2
            drawCircle(color.copy(.08f + p * .04f), 3f + p * 2f, Offset(w*.85f + cos(a)*w*.12f, h*.1f + sin(a)*h*.12f))
        }
        "chat" -> (0..4).forEach { i ->
            drawCircle(color.copy(.07f), 40f + i * 15f, Offset(w * (.15f + i * .18f), h * .9f + sin(anim * PI.toFloat() + i) * 20f))
        }
        "coins","store" -> (0..10).forEach { i ->
            val t = (anim + i * .1f) % 1f
            drawCircle(color.copy(if (t > .9f) (1f - t) * 10f * .12f else .12f), 6f, Offset(w * (.1f + (i % 5) * .2f), h * (1f - t)))
        }
        "mood" -> (0..20).forEach { i ->
            drawCircle(color.copy(.05f), 20f, Offset(w * i / 20f, h * .7f + sin(anim * 2 * PI.toFloat() + i * .5f) * 30f))
        }
        else -> drawCircle(color.copy(.08f), w * .5f, Offset(w * .5f, -w * .2f))
    }
}

fun getSectionColors(section: String, isDark: Boolean): Pair<List<Color>, Color> = when (section) {
    "home" -> if (isDark) listOf(Color(0xFF0F0A1A), Color(0xFF150D28), Color(0xFF1A0D30)) to Color(0xFF8B5CF6)
              else listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FE), Color(0xFFDDD6FE)) to Color(0xFF7C3AED)
    "games","entertainment" -> if (isDark) listOf(Color(0xFF0A0A1A), Color(0xFF1A0D28)) to Color(0xFF6366F1)
                               else listOf(Color(0xFFF0F0FF), Color(0xFFDDD6FE)) to Color(0xFF4F46E5)
    "islamic" -> if (isDark) listOf(Color(0xFF030D0A), Color(0xFF0A1F18)) to Color(0xFF10B981)
                 else listOf(Color(0xFFF0FDF4), Color(0xFFBBF7D0)) to Color(0xFF059669)
    "chat" -> if (isDark) listOf(Color(0xFF1A0A15), Color(0xFF250D1E)) to Color(0xFFEC4899)
              else listOf(Color(0xFFFFF0F6), Color(0xFFFCCFE8)) to Color(0xFFDB2777)
    "coins","store" -> if (isDark) listOf(Color(0xFF1A1400), Color(0xFF25200A)) to Color(0xFFF59E0B)
                       else listOf(Color(0xFFFFFBEB), Color(0xFFFDE68A)) to Color(0xFFD97706)
    "mood" -> if (isDark) listOf(Color(0xFF150A1A), Color(0xFF1E0D28)) to Color(0xFFA78BFA)
              else listOf(Color(0xFFFAF5FF), Color(0xFFE9D5FF)) to Color(0xFF9333EA)
    "tasks" -> if (isDark) listOf(Color(0xFF030D0A), Color(0xFF0D1F18)) to Color(0xFF34D399)
               else listOf(Color(0xFFF0FDF4), Color(0xFFA7F3D0)) to Color(0xFF10B981)
    "settings" -> if (isDark) listOf(Color(0xFF0F0F12), Color(0xFF1A1A24)) to Color(0xFF6B7280)
                  else listOf(Color(0xFFF9FAFB), Color(0xFFE5E7EB)) to Color(0xFF4B5563)
    else -> if (isDark) listOf(Color(0xFF0F0A1A), Color(0xFF150D28)) to Color(0xFF8B5CF6)
            else listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FE)) to Color(0xFF7C3AED)
}
