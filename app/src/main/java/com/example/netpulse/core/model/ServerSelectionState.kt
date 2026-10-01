
package com.example.netpulse.core.model

sealed interface ServerSelectionState {
    data object Loading : ServerSelectionState
    data class Selected(val server: TestServer, val latencyMs: Double) : ServerSelectionState
    data class Available(val servers: List<TestServer>) : ServerSelectionState
    data class Failed(val error: TestError) : ServerSelectionState
}
