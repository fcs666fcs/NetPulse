
package com.example.netpulse.core.model

data class TestServer(
    val id: String,
    val name: String,
    val region: String,
    val baseUrl: String,
    val weight: Int = 1,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val operator: String? = null,
)
