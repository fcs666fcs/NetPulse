
package com.example.netpulse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.netpulse.core.model.SpeedSample
import com.example.netpulse.core.model.TestPhase
import kotlin.math.max

@Composable
fun SpeedGraph(
    samples: List<SpeedSample>,
    modifier: Modifier = Modifier,
) {
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val lineColor = MaterialTheme.colorScheme.primary
    Canvas(modifier.fillMaxWidth().height(180.dp)) {
        repeat(4) { index ->
            val y = size.height * index / 3f
            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
        }
        if (samples.size < 2) return@Canvas
        val phaseSamples = samples.takeLast(160)
        val maxValue = max(1.0, phaseSamples.maxOf { it.instantMbps })
        val path = Path()
        phaseSamples.forEachIndexed { index, sample ->
            val x = if (phaseSamples.size == 1) 0f else size.width * index / (phaseSamples.size - 1)
            val y = size.height * (1f - (sample.instantMbps / maxValue).toFloat()).coerceIn(0f, 1f)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, lineColor, style = Stroke(width = 5f, cap = StrokeCap.Round))
        val last = phaseSamples.last()
        val lastX = size.width
        val lastY = size.height * (1f - (last.instantMbps / maxValue).toFloat()).coerceIn(0f, 1f)
        drawCircle(lineColor, radius = 6f, center = Offset(lastX, lastY))
    }
}
