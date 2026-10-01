
package com.example.netpulse.domain.speedtest

import org.junit.Assert.assertEquals
import org.junit.Test

class SpeedMathTest {
    @Test
    fun mbpsFormulaUsesBitsPerSecond() {
        assertEquals(80.0, SpeedMath.mbps(10_000_000, 1.0), 0.0001)
    }

    @Test
    fun emaOnlyAffectsDisplayValue() {
        assertEquals(25.0, SpeedMath.ema(100.0, 0.0, 0.25), 0.0001)
    }
}
