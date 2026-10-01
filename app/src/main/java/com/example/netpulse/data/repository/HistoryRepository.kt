
package com.example.netpulse.data.repository

import com.example.netpulse.core.model.SpeedTestResult
import com.example.netpulse.data.local.HistoryDao
import com.example.netpulse.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {
    fun observeAll(): Flow<List<HistoryEntity>> = dao.observeAll()

    suspend fun insert(result: SpeedTestResult): Long = dao.insert(
        HistoryEntity(
            startedAt = result.startedAt,
            serverId = result.server?.id ?: "unknown",
            serverName = result.server?.name ?: "Unknown",
            downloadMbps = result.downloadMbps,
            uploadMbps = result.uploadMbps,
            pingMs = result.pingMs,
            jitterMs = result.jitterMs,
            lossPct = result.packetLossPct,
            durationMs = result.durationMs,
            transferredBytes = result.transferredBytes,
            networkType = result.networkType,
            ipVersion = result.ipVersion,
        ),
    )

    suspend fun delete(id: Long) = dao.deleteById(id)
}
