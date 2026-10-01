
package com.example.netpulse.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.netpulse.ui.formatSpeed
import com.example.netpulse.ui.speedUnit

@Composable
fun SpeedHero(
    speedMbps: Double,
    phaseLabel: String,
    modifier: Modifier = Modifier,
) {
    val animated by animateFloatAsState(
        targetValue = speedMbps.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "speed",
    )
    Column(modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = phaseLabel.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.2.sp,
        )
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
            Text(
                text = formatSpeed(animated.toDouble()),
                fontSize = 82.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-2).sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = speedUnit(animated.toDouble()),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 15.dp, start = 8.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}
