
package com.example.netpulse.core.model

data class SpeedTestState(
    val phase: TestPhase = TestPhase.IDLE,
    val currentMbps: Double = 0.0,
    val averageMbps: Double = 0.0,
    val peakMbps: Double = 0.0,
    val pingMs: Double? = null,
    val jitterMs: Double? = null,
    val packetLossPct: Double? = null,
    val progress: Float = 0f,
    val server: TestServer? = null,
    val error: TestError? = null,
    val elapsedMs: Long = 0L,
    val transferredBytes: Long = 0L,
)
