
package com.example.netpulse.domain.speedtest

import com.example.netpulse.core.model.TestServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class PingTester(
    private val client: OkHttpClient,
) {
    data class Result(val pingMs: Double, val jitterMs: Double, val lossPct: Double, val rtts: List<Double>)

    suspend fun run(server: TestServer, count: Int = 12): Result = withContext(Dispatchers.IO) {
        val rtts = mutableListOf<Double>()
        var failed = 0
        repeat(count) { seq ->
            coroutineContext.ensureActive()
            val request = Request.Builder()
                .url("${server.baseUrl.trimEnd('/')}/empty?seq=$seq&t=${System.nanoTime()}")
                .header("Cache-Control", "no-cache")
                .get()
                .build()
            val started = System.nanoTime()
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        failed++
                    } else {
                        rtts += (System.nanoTime() - started) / 1_000_000.0
                    }
                }
            } catch (_: Exception) {
                failed++
            }
        }
        val ping = rtts.sorted().medianOrZero()
        val jitter = JitterCalculator.meanAbsoluteDifference(rtts)
        val loss = if (count == 0) 0.0 else failed * 100.0 / count
        Result(ping, jitter, loss, rtts)
    }

    private fun List<Double>.medianOrZero(): Double {
        if (isEmpty()) return 0.0
        val sorted = sorted()
        return if (size % 2 == 1) sorted[size / 2]
        else (sorted[size / 2 - 1] + sorted[size / 2]) / 2.0
    }
}
