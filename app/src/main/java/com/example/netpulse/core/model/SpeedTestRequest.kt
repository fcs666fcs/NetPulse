
package com.example.netpulse.core.model

data class SpeedTestRequest(
    val durationSeconds: Int = 10,
    val preferredServerId: String? = null,
    val concurrency: Int = 4,
    val demoMode: Boolean = false,
)
