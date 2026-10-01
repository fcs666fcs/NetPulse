
package com.example.netpulse.domain.speedtest

import com.example.netpulse.core.model.ServerSelectionState
import com.example.netpulse.core.model.SpeedSample
import com.example.netpulse.core.model.SpeedTestEvent
import com.example.netpulse.core.model.SpeedTestRequest
import com.example.netpulse.core.model.SpeedTestResult
import com.example.netpulse.core.model.TestPhase
import com.example.netpulse.core.model.TestServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.sin
import kotlin.random.Random

class DemoSpeedTestEngine : SpeedTestEngine {
    private val demoServer = TestServer("demo-tokyo", "Tokyo Demo", "JP", "demo://tokyo")

    override fun selectServer(): Flow<ServerSelectionState> = flow {
        emit(ServerSelectionState.Selected(demoServer, 18.4))
    }

    override fun run(request: SpeedTestRequest): Flow<SpeedTestEvent> = flow {
        val startedAt = System.currentTimeMillis()
        val ping = 18.4 + Random.nextDouble(-1.5, 2.0)
        val jitter = 2.1 + Random.nextDouble(-0.6, 0.8)
        val loss = 0.0
        emit(SpeedTestEvent.PhaseChanged(TestPhase.SELECTING_SERVER, 0.05f))
        delay(250)
        emit(SpeedTestEvent.ServerSelected(demoServer))
        emit(SpeedTestEvent.PhaseChanged(TestPhase.PING, 0.15f))
        repeat(10) { delay(90) }
        emit(SpeedTestEvent.PingUpdated(ping, jitter, loss))
        emit(SpeedTestEvent.PhaseChanged(TestPhase.DOWNLOAD, 0.3f))
        val raw = ResultAggregator().also { it.setPing(ping, jitter, loss) }
        val download = mutableListOf<Double>()
        val upload = mutableListOf<Double>()
        repeat(request.durationSeconds.coerceIn(5, 30) * 5) { i ->
            delay(200)
            val value = 360.0 + sin(i / 3.2) * 55 + Random.nextDouble(-14.0, 14.0)
            val sample = SpeedSample(System.currentTimeMillis(), TestPhase.DOWNLOAD, value, ((i + 1) * 8_800_000L))
            raw.addSample(sample)
            download += value
            emit(SpeedTestEvent.Sample(sample))
        }
        emit(SpeedTestEvent.PhaseChanged(TestPhase.UPLOAD, 0.62f))
        repeat(request.durationSeconds.coerceIn(5, 30) * 5) { i ->
            delay(200)
            val value = 45.0 + sin(i / 4.0) * 10 + Random.nextDouble(-4.0, 4.0)
            val sample = SpeedSample(System.currentTimeMillis(), TestPhase.UPLOAD, value, ((i + 1) * 1_800_000L))
            raw.addSample(sample)
            upload += value
            emit(SpeedTestEvent.Sample(sample))
        }
        emit(SpeedTestEvent.PhaseChanged(TestPhase.ANALYZING, 0.94f))
        delay(500)
        val finishedAt = System.currentTimeMillis()
        emit(
            SpeedTestEvent.Completed(
                SpeedTestResult(
                    startedAt = startedAt,
                    finishedAt = finishedAt,
                    server = demoServer,
                    downloadMbps = download.average(),
                    uploadMbps = upload.average(),
                    pingMs = ping,
                    jitterMs = jitter,
                    packetLossPct = loss,
                    durationMs = finishedAt - startedAt,
                    transferredBytes = 8_800_000L * download.size + 1_800_000L * upload.size,
                    networkType = "Demo",
                    ipVersion = "IPv4",
                    downloadPeakMbps = download.maxOrNull() ?: 0.0,
                    uploadPeakMbps = upload.maxOrNull() ?: 0.0,
                ),
            ),
        )
    }

    override fun cancel() = Unit
}
