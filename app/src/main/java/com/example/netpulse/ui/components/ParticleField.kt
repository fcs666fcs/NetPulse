
package com.example.netpulse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun ParticleField(
    enabled: Boolean,
    intensity: Int,
    modifier: Modifier = Modifier,
) {
    var phase by remember { mutableFloatStateOf(0f) }
    val preview = LocalInspectionMode.current
    val particleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
    LaunchedEffect(enabled, preview) {
        while (enabled && !preview) {
            phase += 0.05f
            delay(32)
        }
    }
    Canvas(modifier.fillMaxSize()) {
        if (!enabled) return@Canvas
        drawParticles(phase, intensity.coerceIn(8, 60), particleColor)
    }
}

private fun DrawScope.drawParticles(phase: Float, count: Int, color: Color) {
    val width = size.width
    val height = size.height
    repeat(count) { i ->
        val lane = i % 7
        val baseX = (i * 89f) % width
        val travel = (phase * (35f + lane * 4f) * density + baseX) % width
        val y = height * (0.2f + (i % 11) / 14f) + sin(phase + i) * 5.dp.toPx()
        val r = (1.2f + (i % 3) * 0.5f).dp.toPx()
        drawCircle(color = color, radius = r, center = Offset(travel, y))
    }
}
