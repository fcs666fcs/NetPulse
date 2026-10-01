
package com.example.netpulse.core.model

sealed interface SpeedTestEvent {
    data class PhaseChanged(val phase: TestPhase, val progress: Float) : SpeedTestEvent
    data class ServerSelected(val server: TestServer) : SpeedTestEvent
    data class PingUpdated(
        val pingMs: Double,
        val jitterMs: Double,
        val lossPct: Double,
    ) : SpeedTestEvent
    data class Sample(val sample: SpeedSample) : SpeedTestEvent
    data class Completed(val result: SpeedTestResult) : SpeedTestEvent
    data class Failed(val error: TestError) : SpeedTestEvent
}
