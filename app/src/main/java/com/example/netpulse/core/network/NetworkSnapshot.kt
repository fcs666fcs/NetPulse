
package com.example.netpulse.core.network

data class NetworkSnapshot(
    val connected: Boolean,
    val transport: String,
    val label: String,
    val ipVersion: String,
)
