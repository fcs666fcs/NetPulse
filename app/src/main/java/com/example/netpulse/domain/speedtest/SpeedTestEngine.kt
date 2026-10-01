
package com.example.netpulse.domain.speedtest

import com.example.netpulse.core.model.ServerSelectionState
import com.example.netpulse.core.model.SpeedTestEvent
import com.example.netpulse.core.model.SpeedTestRequest
import kotlinx.coroutines.flow.Flow

interface SpeedTestEngine {
    fun selectServer(): Flow<ServerSelectionState>
    fun run(request: SpeedTestRequest): Flow<SpeedTestEvent>
    fun cancel()
}
