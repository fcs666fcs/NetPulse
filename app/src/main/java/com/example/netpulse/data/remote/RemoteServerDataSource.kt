
package com.example.netpulse.data.remote

import com.example.netpulse.core.model.TestServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class RemoteServerDataSource(
    private val apiFactory: (String) -> SpeedApi,
) {
    suspend fun getServers(baseUrl: String): List<TestServer> = withContext(Dispatchers.IO) {
        apiFactory(baseUrl.trimEnd('/')).servers().servers.map {
            TestServer(
                id = it.id,
                name = it.name,
                region = it.region,
                baseUrl = it.baseUrl,
                weight = it.weight,
            )
        }
    }

    suspend fun probe(server: TestServer): Double = withContext(Dispatchers.IO) {
        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .callTimeout(3, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("${server.baseUrl.trimEnd('/')}/empty?seq=${System.nanoTime()}")
            .get()
            .header("Cache-Control", "no-cache")
            .build()
        val start = System.nanoTime()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("HTTP ${response.code}")
        }
        (System.nanoTime() - start) / 1_000_000.0
    }
}
