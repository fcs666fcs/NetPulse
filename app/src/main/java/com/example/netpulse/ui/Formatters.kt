
package com.example.netpulse.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatSpeed(mbps: Double): String {
    return when {
        mbps >= 1000 -> String.format(Locale.US, "%.2f", mbps / 1000.0)
        mbps >= 100 -> String.format(Locale.US, "%.1f", mbps)
        else -> String.format(Locale.US, "%.2f", mbps)
    }
}

fun speedUnit(mbps: Double): String = if (mbps >= 1000) "Gbps" else "Mbps"

fun formatMs(value: Double?): String = value?.let { String.format(Locale.US, "%.1f", it) } ?: "—"
fun formatPct(value: Double?): String = value?.let { String.format(Locale.US, "%.1f", it) + "%" } ?: "—"
fun formatDate(epochMs: Long): String = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(epochMs))
fun formatBytes(bytes: Long): String = when {
    bytes >= 1_000_000_000 -> String.format(Locale.US, "%.2f GB", bytes / 1_000_000_000.0)
    bytes >= 1_000_000 -> String.format(Locale.US, "%.1f MB", bytes / 1_000_000.0)
    bytes >= 1_000 -> String.format(Locale.US, "%.1f KB", bytes / 1_000.0)
    else -> "$bytes B"
}
