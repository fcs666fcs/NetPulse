
package com.example.netpulse.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun TestButton(
    testing: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (testing) 0.98f else 1f,
        animationSpec = spring(),
        label = "buttonScale",
    )
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp).graphicsLayer { scaleX = scale; scaleY = scale },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        Text(if (testing) "STOP TEST" else "START TEST")
    }
}
