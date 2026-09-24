package com.nexus.personaldashboard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.sin

enum class SpecialDayType { EID_AL_ADHA, RAMADAN, NATIONAL, MOTHERS_DAY, OTHER }
data class SpecialDay(val name: String, val month: Int, val day: Int, val duration: Int = 1, val type: SpecialDayType)

val specialDays = listOf(
    SpecialDay("Mother's Day", 3, 21, 1, SpecialDayType.MOTHERS_DAY),
    SpecialDay("Eid Al-Adha", 6, 7, 4, SpecialDayType.EID_AL_ADHA),
    SpecialDay("National Day", 7, 23, 1, SpecialDayType.NATIONAL),
)

fun getCurrentSpecialDay(): SpecialDay? {
    val cal = Calendar.getInstance()
    val month = cal.get(Calendar.MONTH) + 1
    val day = cal.get(Calendar.DAY_OF_MONTH)
    return specialDays.firstOrNull { sd -> month == sd.month && day >= sd.day && day < sd.day + sd.duration }
}

@Composable
fun SpecialDayOverlay() {
    val specialDay = remember { getCurrentSpecialDay() } ?: return
    when (specialDay.type) {
        SpecialDayType.EID_AL_ADHA -> EidSheepAnimation()
        SpecialDayType.RAMADAN -> RamadanDecoration()
        SpecialDayType.MOTHERS_DAY -> MothersDay()
        else -> Unit
    }
}

@Composable
fun EidSheepAnimation() {
    val transition = rememberInfiniteTransition(label = "eid")
    val anim by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart), "sheep")
    val bob by transition.animateFloat(0f, 8f, infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse), "bob")
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        for (i in 0..2) {
            val x = w * ((anim + i * 0.33f) % 1f); val y = h - 120f + bob - i * 10f
            drawCircle(Color(0xFFEEEEEE), 22f, Offset(x, y))
            drawCircle(Color(0xFFDDDDDD), 13f, Offset(x + 22f, y - 8f))
            drawCircle(Color.Black, 2f, Offset(x + 28f, y - 10f))
            drawCircle(Color.White.copy(.8f), 8f, Offset(x - 8f, y - 8f))
            drawCircle(Color.White.copy(.8f), 7f, Offset(x + 8f, y + 6f))
            for (leg in 0..3) drawLine(Color(0xFF888888), Offset(x - 15f + leg * 10f, y + 20f), Offset(x - 15f + leg * 10f, y + 32f), 3f)
        }
        for (i in 0..6) {
            val pulse = (sin(anim * 2 * PI.toFloat() + i) + 1) / 2
            drawCircle(Color(0xFFF59E0B).copy(.5f + pulse * .3f), 4f + pulse * 3f, Offset(w * (i.toFloat() / 6f), h * .05f + sin(anim * 2 * PI.toFloat() + i) * 10f))
        }
    }
}

@Composable
fun RamadanDecoration() {
    val transition = rememberInfiniteTransition(label = "ramadan")
    val anim by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), "rstars")
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        for (i in 0..14) {
            val pulse = (sin(anim * 2 * PI.toFloat() * 1.5f + i) + 1) / 2
            drawCircle(Color(0xFFF59E0B).copy(.3f + pulse * .2f), 3f + pulse * 2f, Offset(w * (i.toFloat() / 14f), h * .08f + sin(anim * 2 * PI.toFloat() + i * .5f) * 8f))
        }
    }
}

@Composable
fun MothersDay() {
    val transition = rememberInfiniteTransition(label = "mothers")
    val anim by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart), "hearts")
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        for (i in 0..5) {
            val t = (anim + i * .17f) % 1f
            drawCircle(Color(0xFFEC4899).copy((1f - t) * .3f), 6f + t * 4f, Offset(w * (.1f + i * .16f), h * (1f - t * .5f)))
        }
    }
}
