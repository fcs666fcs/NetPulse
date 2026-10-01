
package com.example.netpulse.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Palette = lightColorScheme(
    primary = Color(0xFF1A73E8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7E8FF),
    onPrimaryContainer = Color(0xFF001A33),
    surface = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF1F3F4),
    background = Color(0xFFFAFBFC),
    onBackground = Color(0xFF202124),
    onSurface = Color(0xFF202124),
    secondary = Color(0xFF5F6368),
    error = Color(0xFFB3261E),
)

@Composable
fun NetPulseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Palette,
        typography = NetPulseTypography,
        content = content,
    )
}
