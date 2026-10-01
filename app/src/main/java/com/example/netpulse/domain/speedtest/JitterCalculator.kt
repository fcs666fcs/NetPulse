
package com.example.netpulse.domain.speedtest

object JitterCalculator {
    /**
     * NetPulse v1 formula: mean absolute difference between adjacent RTT samples.
     * This definition is intentionally fixed across UI, client and server documentation.
     */
    fun meanAbsoluteDifference(samples: List<Double>): Double {
        if (samples.size < 2) return 0.0
        var sum = 0.0
        for (i in 1 until samples.size) sum += kotlin.math.abs(samples[i] - samples[i - 1])
        return sum / (samples.size - 1)
    }
}
