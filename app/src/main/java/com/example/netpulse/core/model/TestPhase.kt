
package com.example.netpulse.core.model

enum class TestPhase {
    IDLE,
    SELECTING_SERVER,
    PING,
    DOWNLOAD,
    UPLOAD,
    ANALYZING,
    COMPLETED,
    CANCELLED,
    ERROR,
}
