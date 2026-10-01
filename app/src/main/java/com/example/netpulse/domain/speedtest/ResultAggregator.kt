
package com.example.netpulse.domain.speedtest

import com.example.netpulse.core.model.SpeedSample
import com.example.netpulse.core.model.SpeedTestResult
import com.example.netpulse.core.model.TestServer
import com.example.netpulse.core.model.TestPhase

class ResultAggregator {
    private val download = mutableListOf<Double>()
    private val upload = mutableListOf<Double>()
    private var pingMs: Double? = null
    private var jitterMs: Double? = null
    private var lossPct: Double? = null
    private var transferredBytes = 0L

    fun addSample(sample: SpeedSample) {
        when (sample.phase) {
            TestPhase.DOWNLOAD -> download += sample.instantMbps
            TestPhase.UPLOAD -> upload += sample.instantMbps
            else -> Unit
        }
        transferredBytes = maxOf(transferredBytes, sample.bytes)
    }

    fun setPing(ping: Double?, jitter: Double?, loss: Double?) {
        pingMs = ping
        jitterMs = jitter
        lossPct = loss
    }

    fun build(
        startedAt: Long,
        server: TestServer?,
        finishedAt: Long,
        networkType: String?,
        ipVersion: String?,
    ): SpeedTestResult {
        return SpeedTestResult(
            startedAt = startedAt,
            finishedAt = finishedAt,
            server = server,
            downloadMbps = download.averageOrZero(),
            uploadMbps = upload.averageOrZero(),
            pingMs = pingMs,
            jitterMs = jitterMs,
            packetLossPct = lossPct,
            durationMs = finishedAt - startedAt,
            transferredBytes = transferredBytes,
            networkType = networkType,
            ipVersion = ipVersion,
            downloadPeakMbps = download.maxOrZero(),
            uploadPeakMbps = upload.maxOrZero(),
        )
    }

    private fun List<Double>.averageOrZero(): Double = if (isEmpty()) 0.0 else average()
    private fun List<Double>.maxOrZero(): Double = maxOrNull() ?: 0.0
}
