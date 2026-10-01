
package com.example.netpulse.domain.speedtest

import org.junit.Assert.assertEquals
import org.junit.Test

class JitterCalculatorTest {
    @Test
    fun meanAbsoluteDifference() {
        assertEquals(2.0, JitterCalculator.meanAbsoluteDifference(listOf(10.0, 12.0, 10.0, 14.0)), 0.0001)
    }

    @Test
    fun emptyOrSingleIsZero() {
        assertEquals(0.0, JitterCalculator.meanAbsoluteDifference(emptyList()), 0.0001)
        assertEquals(0.0, JitterCalculator.meanAbsoluteDifference(listOf(10.0)), 0.0001)
    }
}
