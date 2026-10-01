
package com.example.netpulse.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.netpulse.NetPulseApplication
import com.example.netpulse.core.model.SpeedSample
import com.example.netpulse.core.model.SpeedTestEvent
import com.example.netpulse.core.model.SpeedTestRequest
import com.example.netpulse.core.model.SpeedTestResult
import com.example.netpulse.core.model.SpeedTestState
import com.example.netpulse.core.model.TestPhase
import com.example.netpulse.data.local.AppSettings
import com.example.netpulse.data.local.HistoryEntity
import com.example.netpulse.data.local.SettingsRepository
import com.example.netpulse.domain.speedtest.SpeedMath
import com.example.netpulse.domain.speedtest.SpeedTestEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as NetPulseApplication
    private val settingsRepository: SettingsRepository = app.container.settingsRepository
    private var engine: SpeedTestEngine? = null
    private var testJob: Job? = null
    private var displaySpeed = 0.0

    private val _testState = MutableStateFlow(SpeedTestState())
    val testState: StateFlow<SpeedTestState> = _testState.asStateFlow()

    private val _samples = MutableStateFlow<List<SpeedSample>>(emptyList())
    val samples: StateFlow<List<SpeedSample>> = _samples.asStateFlow()

    private val _lastResult = MutableStateFlow<SpeedTestResult?>(null)
    val lastResult: StateFlow<SpeedTestResult?> = _lastResult.asStateFlow()
    private val _selectedHistory = MutableStateFlow<HistoryEntity?>(null)
    val selectedHistory: StateFlow<HistoryEntity?> = _selectedHistory.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AppSettings(),
    )

    val history: StateFlow<List<HistoryEntity>> = app.container.historyRepository.observeAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
    )

    fun startTest() {
        if (testJob?.isActive == true) return
        testJob = viewModelScope.launch {
            val currentSettings = settingsRepository.settings.first()
            engine = app.container.createEngine(currentSettings)
            _samples.value = emptyList()
            displaySpeed = 0.0
            _testState.value = SpeedTestState(phase = TestPhase.SELECTING_SERVER)
            val request = SpeedTestRequest(
                durationSeconds = currentSettings.durationSeconds,
                preferredServerId = currentSettings.preferredServerId,
                concurrency = 4,
                demoMode = currentSettings.demoMode,
            )
            engine!!.run(request).collect(::handleEvent)
        }
    }

    private fun handleEvent(event: SpeedTestEvent) {
        when (event) {
            is SpeedTestEvent.PhaseChanged -> _testState.value = _testState.value.copy(
                phase = event.phase,
                progress = event.progress,
            )
            is SpeedTestEvent.ServerSelected -> _testState.value = _testState.value.copy(server = event.server)
            is SpeedTestEvent.PingUpdated -> _testState.value = _testState.value.copy(
                pingMs = event.pingMs,
                jitterMs = event.jitterMs,
                packetLossPct = event.lossPct,
            )
            is SpeedTestEvent.Sample -> {
                val sample = event.sample
                displaySpeed = SpeedMath.ema(sample.instantMbps, displaySpeed, 0.28)
                val current = _testState.value
                _testState.value = current.copy(
                    currentMbps = displaySpeed,
                    averageMbps = averageForPhase(sample.phase),
                    peakMbps = maxForPhase(sample.phase),
                    elapsedMs = (sample.timestampMs - (_samples.value.firstOrNull()?.timestampMs ?: sample.timestampMs)).coerceAtLeast(0),
                    transferredBytes = sample.bytes,
                )
                _samples.value = (_samples.value + sample).takeLast(240)
            }
            is SpeedTestEvent.Completed -> {
                _lastResult.value = event.result
                _selectedHistory.value = null
                _testState.value = _testState.value.copy(
                    phase = TestPhase.COMPLETED,
                    progress = 1f,
                    currentMbps = event.result.downloadMbps,
                    averageMbps = event.result.downloadMbps,
                    peakMbps = event.result.downloadPeakMbps,
                    pingMs = event.result.pingMs,
                    jitterMs = event.result.jitterMs,
                    packetLossPct = event.result.packetLossPct,
                    server = event.result.server,
                    elapsedMs = event.result.durationMs,
                    transferredBytes = event.result.transferredBytes,
                )
                viewModelScope.launch { app.container.historyRepository.insert(event.result) }
            }
            is SpeedTestEvent.Failed -> _testState.value = _testState.value.copy(
                phase = TestPhase.ERROR,
                error = event.error,
            )
        }
    }

    private fun averageForPhase(phase: TestPhase): Double =
        _samples.value.filter { it.phase == phase }.map { it.instantMbps }.averageOrZero()

    private fun maxForPhase(phase: TestPhase): Double =
        _samples.value.filter { it.phase == phase }.maxOfOrNull { it.instantMbps } ?: 0.0

    fun stopTest() {
        engine?.cancel()
        testJob?.cancel()
        testJob = null
        _testState.value = _testState.value.copy(phase = TestPhase.CANCELLED)
    }

    fun retry() {
        stopTest()
        startTest()
    }

    fun reset() {
        stopTest()
        _testState.value = SpeedTestState()
        _lastResult.value = null
        _selectedHistory.value = null
        _samples.value = emptyList()
    }

    fun setDuration(value: Int) = viewModelScope.launch { settingsRepository.setDuration(value) }
    fun setAnimation(value: String) = viewModelScope.launch { settingsRepository.setAnimation(value) }
    fun setDemoMode(value: Boolean) = viewModelScope.launch { settingsRepository.setDemoMode(value) }
    fun setServerUrl(value: String) = viewModelScope.launch { settingsRepository.setServerUrl(value) }
    fun deleteHistory(id: Long) = viewModelScope.launch { app.container.historyRepository.delete(id) }
    fun selectHistory(item: HistoryEntity) {
        _lastResult.value = null
        _selectedHistory.value = item
    }

    private fun List<Double>.averageOrZero(): Double = if (isEmpty()) 0.0 else average()
}
