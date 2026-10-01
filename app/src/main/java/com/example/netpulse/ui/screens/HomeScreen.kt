
package com.example.netpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.netpulse.core.model.SpeedTestState
import com.example.netpulse.core.model.TestPhase
import com.example.netpulse.ui.components.MetricCard
import com.example.netpulse.ui.components.ParticleField
import com.example.netpulse.ui.components.ServerCard
import com.example.netpulse.ui.components.SpeedGraph
import com.example.netpulse.ui.components.SpeedHero
import com.example.netpulse.ui.components.TestButton
import com.example.netpulse.ui.speedUnit
import com.example.netpulse.ui.formatSpeed
import com.example.netpulse.ui.formatMs
import com.example.netpulse.ui.formatPct

@Composable
fun HomeScreen(
    state: SpeedTestState,
    samples: List<com.example.netpulse.core.model.SpeedSample>,
    demoMode: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onSettings: () -> Unit,
) {
    val testing = state.phase.isTesting()
    val phaseLabel = when (state.phase) {
        TestPhase.DOWNLOAD -> "Download"
        TestPhase.UPLOAD -> "Upload"
        TestPhase.PING -> "Ping"
        TestPhase.ANALYZING -> "Analyzing"
        TestPhase.COMPLETED -> "Completed"
        TestPhase.ERROR -> "Error"
        TestPhase.CANCELLED -> "Stopped"
        else -> "Ready"
    }
    Column(Modifier.fillMaxSize()) {
        com.example.netpulse.ui.components.AppTopBar("NetPulse", onSettings)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Network dashboard", style = MaterialTheme.typography.titleMedium)
                AssistChip(onClick = {}, label = { Text(if (demoMode) "DEMO" else "LIVE") })
            }
            Box(Modifier.fillMaxWidth().height(130.dp)) {
                ParticleField(enabled = testing && !demoMode, intensity = 30, modifier = Modifier.fillMaxSize())
                SpeedHero(
                    speedMbps = state.currentMbps,
                    phaseLabel = phaseLabel,
                )
            }
            AnimatedVisibility(testing) {
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                )
            }
            Card(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("LIVE GRAPH", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                    SpeedGraph(samples = samples)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricCard("PING", formatMs(state.pingMs), "ms")
                MetricCard("JITTER", formatMs(state.jitterMs), "ms")
                MetricCard("LOSS", formatPct(state.packetLossPct))
            }
            Spacer(Modifier.height(12.dp))
            ServerCard(state.server, "${if (state.server != null) "Connected" else "Network"} · ${state.server?.region ?: "Auto"}")
            Spacer(Modifier.height(16.dp))
            TestButton(testing = testing, onClick = if (testing) onStop else onStart)
            Spacer(Modifier.height(18.dp))
            Text(
                "结果由原始采样聚合生成；动画只负责显示，不修改最终统计值。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it.message, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun TestPhase.isTesting(): Boolean = this in setOf(
    TestPhase.SELECTING_SERVER,
    TestPhase.PING,
    TestPhase.DOWNLOAD,
    TestPhase.UPLOAD,
    TestPhase.ANALYZING,
)
