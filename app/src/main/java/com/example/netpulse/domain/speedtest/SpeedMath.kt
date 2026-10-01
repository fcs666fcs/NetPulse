
package com.example.netpulse.domain.speedtest

object SpeedMath {
    fun bitsPerSecond(bytesDelta: Long, deltaSeconds: Double): Double {
        if (deltaSeconds <= 0.0) return 0.0
        return bytesDelta.toDouble() * 8.0 / deltaSeconds
    }

    fun mbps(bytesDelta: Long, deltaSeconds: Double): Double =
        bitsPerSecond(bytesDelta, deltaSeconds) / 1_000_000.0

    fun ema(raw: Double, previous: Double, alpha: Double = 0.25): Double =
        alpha * raw + (1.0 - alpha) * previous
}
