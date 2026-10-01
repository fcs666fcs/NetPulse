
package com.example.netpulse.core.model

data class SpeedTestResult(
    val startedAt: Long,
    val finishedAt: Long,
    val server: TestServer?,
    val downloadMbps: Double,
    val uploadMbps: Double,
    val pingMs: Double?,
    val jitterMs: Double?,
    val packetLossPct: Double?,
    val durationMs: Long,
    val transferredBytes: Long,
    val networkType: String?,
    val ipVersion: String? = null,
    val downloadPeakMbps: Double = 0.0,
    val uploadPeakMbps: Double = 0.0,
)
