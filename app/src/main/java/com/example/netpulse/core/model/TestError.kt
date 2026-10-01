
package com.example.netpulse.core.model

enum class TestErrorCode {
    NO_NETWORK,
    SERVER_TIMEOUT,
    SERVER_BUSY,
    TEST_CANCELLED,
    TLS_ERROR,
    UNKNOWN,
}

data class TestError(
    val code: TestErrorCode,
    val message: String,
    val retryable: Boolean = true,
)
