
package com.example.netpulse.domain.speedtest

import com.example.netpulse.core.model.SpeedSample
import com.example.netpulse.core.model.TestPhase
import com.example.netpulse.core.model.TestServer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.coroutineContext

class DownloadTester(
    private val client: OkHttpClient,
) {
    suspend fun run(
        server: TestServer,
        durationSeconds: Int,
        concurrency: Int,
        onSample: suspend (SpeedSample) -> Unit,
    ) = coroutineScope {
        val totalBytes = AtomicLong(0)
        val jobs = (0 until concurrency.coerceIn(1, 8)).map {
            async(Dispatchers.IO) {
                while (coroutineContext.isActive) {
                    val request = Request.Builder()
                        .url("${server.baseUrl.trimEnd('/')}/download?bytes=16777216&token=${System.nanoTime()}")
                        .header("Cache-Control", "no-cache")
                        .get()
                        .build()
                    try {
                        client.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) return@use
                            response.body.byteStream().use { input ->
                                val buffer = ByteArray(64 * 1024)
                                while (true) {
                                    coroutineContext.ensureActive()
                                    val read = input.read(buffer)
                                    if (read < 0) break
                                    if (read > 0) totalBytes.addAndGet(read.toLong())
                                }
                            }
                        }
                    } catch (_: Exception) {
                        delay(80)
                    }
                }
            }
        }

        val startedNs = System.nanoTime()
        var previousNs = startedNs
        var previousBytes = 0L
        try {
            while ((System.nanoTime() - startedNs) / 1_000_000_000L < durationSeconds) {
                delay(200)
                coroutineContext.ensureActive()
                val now = System.nanoTime()
                val currentBytes = totalBytes.get()
                val deltaSeconds = (now - previousNs) / 1_000_000_000.0
                val mbps = SpeedMath.mbps(currentBytes - previousBytes, deltaSeconds)
                onSample(
                    SpeedSample(
                        timestampMs = System.currentTimeMillis(),
                        phase = TestPhase.DOWNLOAD,
                        instantMbps = mbps,
                        bytes = currentBytes,
                    ),
                )
                previousNs = now
                previousBytes = currentBytes
            }
        } finally {
            jobs.forEach(Job::cancel)
            jobs.awaitAll()
        }
    }
}
