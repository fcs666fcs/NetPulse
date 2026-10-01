
package com.example.netpulse.data.repository

import com.example.netpulse.core.model.TestError
import com.example.netpulse.core.model.TestErrorCode
import com.example.netpulse.core.model.TestServer
import com.example.netpulse.data.remote.RemoteServerDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServerRepository(
    private val remote: RemoteServerDataSource,
) {
    suspend fun fetchServers(baseUrl: String): List<TestServer> = remote.getServers(baseUrl)

    suspend fun pickBest(
        servers: List<TestServer>,
        preferredId: String?,
    ): Pair<TestServer, Double> = withContext(Dispatchers.IO) {
        preferredId?.let { id -> servers.firstOrNull { it.id == id }?.let { return@withContext it to remote.probe(it) } }
        val scored = servers.mapNotNull { server ->
            runCatching { server to remote.probe(server) }.getOrNull()
        }
        if (scored.isEmpty()) throw IllegalStateException(TestErrorCode.SERVER_TIMEOUT.name)
        scored.minBy { it.second }
    }
}
