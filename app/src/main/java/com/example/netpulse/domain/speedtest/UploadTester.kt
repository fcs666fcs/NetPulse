
package com.example.netpulse.domain.speedtest

import com.example.netpulse.core.model.SpeedSample
import com.example.netpulse.core.model.TestPhase
import com.example.netpulse.core.model.TestServer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.coroutineContext

class UploadTester(
    private val client: OkHttpClient,
) {
    private val contentType = "application/octet-stream".toMediaType()
    private val payload = ByteArray(1024 * 1024) { i -> ((i * 31 + 17) and 0xFF).toByte() }

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
                    val body = object : RequestBody() {
                        override fun contentType() = this@UploadTester.contentType
                        override fun contentLength() = 8L * 1024L * 1024L
                        override fun writeTo(sink: BufferedSink) {
                            var remaining = contentLength()
                            while (remaining > 0) {
                                val count = minOf(remaining, payload.size.toLong()).toInt()
                                sink.write(payload, 0, count)
                                sink.flush()
                                remaining -= count
                                totalBytes.addAndGet(count.toLong())
                            }
                        }
                    }
                    val request = Request.Builder()
                        .url("${server.baseUrl.trimEnd('/')}/upload?token=${System.nanoTime()}")
                        .post(body)
                        .header("Cache-Control", "no-cache")
                        .build()
                    try {
                        client.newCall(request).execute().use { }
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
                        phase = TestPhase.UPLOAD,
                        instantMbps = mbps,
                        bytes = currentBytes,
                    ),
                )
                previousNs = now
                previousBytes = currentBytes
            }
        } finally {
            jobs.forEach { it.cancel() }
            jobs.awaitAll()
        }
    }
}
