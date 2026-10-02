package com.example.netpulse.domain.speedtest

import com.example.netpulse.core.model.ServerSelectionState
import com.example.netpulse.core.model.SpeedTestEvent
import com.example.netpulse.core.model.SpeedTestRequest
import com.example.netpulse.core.model.TestError
import com.example.netpulse.core.model.TestErrorCode
import com.example.netpulse.core.model.TestPhase
import com.example.netpulse.core.network.NetworkMonitor
import com.example.netpulse.data.repository.ServerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext

class DefaultSpeedTestEngine(
    private val serverRepository: ServerRepository,
    private val pingTester: PingTester,
    private val downloadTester: DownloadTester,
    private val uploadTester: UploadTester,
    private val networkMonitor: NetworkMonitor,
    private val configuredApiUrl: String,
) : SpeedTestEngine {

    private var activeJob: Job? = null

    override fun selectServer(): Flow<ServerSelectionState> = flow {
        emit(ServerSelectionState.Loading)

        val servers = serverRepository.fetchServers(configuredApiUrl)

        if (servers.isEmpty()) {
            emit(
                ServerSelectionState.Failed(
                    TestError(
                        TestErrorCode.SERVER_TIMEOUT,
                        "No servers returned",
                    ),
                ),
            )
            return@flow
        }

        emit(ServerSelectionState.Available(servers))

        val (server, rtt) = serverRepository.pickBest(
            servers,
            null,
        )

        emit(
            ServerSelectionState.Selected(
                server,
                rtt,
            ),
        )
    }.catch { throwable ->
        emit(
            ServerSelectionState.Failed(
                TestError(
                    TestErrorCode.SERVER_TIMEOUT,
                    throwable.message ?: "Server unavailable",
                ),
            ),
        )
    }

    override fun run(request: SpeedTestRequest): Flow<SpeedTestEvent> = flow {
        // Use the current coroutine context rather than the old
        // kotlin.coroutines.coroutineContext property.
        activeJob = currentCoroutineContext().job

        val snapshot = networkMonitor.snapshot()

        if (!snapshot.connected) {
            emit(
                SpeedTestEvent.Failed(
                    TestError(
                        TestErrorCode.NO_NETWORK,
                        "没有可用网络",
                    ),
                ),
            )
            return@flow
        }

        val startedAt = System.currentTimeMillis()
        val aggregator = ResultAggregator()

        emit(
            SpeedTestEvent.PhaseChanged(
                TestPhase.SELECTING_SERVER,
                0.04f,
            ),
        )

        val servers = serverRepository.fetchServers(configuredApiUrl)

        if (servers.isEmpty()) {
            throw IllegalStateException("NO_SERVER")
        }

        val (server, _) = serverRepository.pickBest(
            servers,
            request.preferredServerId,
        )

        emit(
            SpeedTestEvent.ServerSelected(server),
        )

        emit(
            SpeedTestEvent.PhaseChanged(
                TestPhase.PING,
                0.12f,
            ),
        )

        val ping = pingTester.run(server)

        aggregator.setPing(
            ping.pingMs,
            ping.jitterMs,
            ping.lossPct,
        )

        emit(
            SpeedTestEvent.PingUpdated(
                ping.pingMs,
                ping.jitterMs,
                ping.lossPct,
            ),
        )

        emit(
            SpeedTestEvent.PhaseChanged(
                TestPhase.DOWNLOAD,
                0.28f,
            ),
        )

        downloadTester.run(
            server = server,
            durationSeconds = request.durationSeconds,
            concurrency = request.concurrency,
        ) { sample ->
            aggregator.addSample(sample)
            emit(
                SpeedTestEvent.Sample(sample),
            )
        }

        emit(
            SpeedTestEvent.PhaseChanged(
                TestPhase.UPLOAD,
                0.62f,
            ),
        )

        uploadTester.run(
            server = server,
            durationSeconds = request.durationSeconds,
            concurrency = request.concurrency,
        ) { sample ->
            aggregator.addSample(sample)
            emit(
                SpeedTestEvent.Sample(sample),
            )
        }

        emit(
            SpeedTestEvent.PhaseChanged(
                TestPhase.ANALYZING,
                0.94f,
            ),
        )

        withContext(Dispatchers.Default) {
            delay(350)
        }

        val finalSnapshot = networkMonitor.snapshot()

        val result = aggregator.build(
            startedAt = startedAt,
            server = server,
            finishedAt = System.currentTimeMillis(),
            networkType = finalSnapshot.transport,
            ipVersion = finalSnapshot.ipVersion,
        )

        emit(
            SpeedTestEvent.Completed(result),
        )
    }.catch { throwable ->
        val code = when {
            throwable is java.net.SocketTimeoutException -> {
                TestErrorCode.SERVER_TIMEOUT
            }

            throwable is javax.net.ssl.SSLException -> {
                TestErrorCode.TLS_ERROR
            }

            throwable.message?.contains("429") == true ||
                throwable.message?.contains("503") == true -> {
                TestErrorCode.SERVER_BUSY
            }

            throwable.message?.contains(
                "timeout",
                ignoreCase = true,
            ) == true -> {
                TestErrorCode.SERVER_TIMEOUT
            }

            else -> {
                TestErrorCode.UNKNOWN
            }
        }

        emit(
            SpeedTestEvent.Failed(
                TestError(
                    code = code,
                    message = throwable.message ?: "测试未完成，请重试",
                ),
            ),
        )
    }

    override fun cancel() {
        activeJob?.cancel()
        activeJob = null
    }
}
