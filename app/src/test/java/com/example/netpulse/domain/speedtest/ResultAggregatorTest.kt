
package com.example.netpulse.domain.speedtest

import com.example.netpulse.core.model.SpeedSample
import com.example.netpulse.core.model.TestPhase
import org.junit.Assert.assertEquals
import org.junit.Test

class ResultAggregatorTest {
    @Test
    fun finalDownloadAndUploadAreAverages() {
        val aggregator = ResultAggregator()
        aggregator.setPing(18.0, 2.0, 0.0)
        aggregator.addSample(SpeedSample(1L, TestPhase.DOWNLOAD, 100.0, 1000L))
        aggregator.addSample(SpeedSample(2L, TestPhase.DOWNLOAD, 200.0, 2000L))
        aggregator.addSample(SpeedSample(3L, TestPhase.UPLOAD, 20.0, 300L))
        aggregator.addSample(SpeedSample(4L, TestPhase.UPLOAD, 40.0, 400L))

        val result = aggregator.build(0L, null, 1000L, "Wi-Fi", "IPv4")
        assertEquals(150.0, result.downloadMbps, 0.0001)
        assertEquals(30.0, result.uploadMbps, 0.0001)
        assertEquals(200.0, result.downloadPeakMbps, 0.0001)
        assertEquals(18.0, result.pingMs!!, 0.0001)
    }
}
