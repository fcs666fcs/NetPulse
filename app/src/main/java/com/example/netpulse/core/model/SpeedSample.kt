
package com.example.netpulse.core.model

data class SpeedSample(
    val timestampMs: Long,
    val phase: TestPhase,
    val instantMbps: Double,
    val bytes: Long,
)
