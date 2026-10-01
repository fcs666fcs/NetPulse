
package com.example.netpulse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long,
    val serverId: String,
    val serverName: String,
    val downloadMbps: Double,
    val uploadMbps: Double,
    val pingMs: Double?,
    val jitterMs: Double?,
    val lossPct: Double?,
    val durationMs: Long,
    val transferredBytes: Long,
    val networkType: String?,
    val ipVersion: String?,
)
