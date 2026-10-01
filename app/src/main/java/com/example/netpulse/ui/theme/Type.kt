
package com.example.netpulse.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val NetPulseTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium),
        headlineLarge = headlineLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold),
        titleLarge = titleLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold),
        bodyLarge = bodyLarge.copy(fontFamily = FontFamily.SansSerif, fontSize = 16.sp),
        labelMedium = labelMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium),
    )
}
