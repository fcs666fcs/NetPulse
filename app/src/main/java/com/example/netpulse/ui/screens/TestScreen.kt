
package com.example.netpulse.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.netpulse.core.model.SpeedSample
import com.example.netpulse.core.model.SpeedTestState
import com.example.netpulse.core.model.TestPhase
import com.example.netpulse.ui.components.ParticleField
import com.example.netpulse.ui.components.SpeedGraph
import com.example.netpulse.ui.components.SpeedHero
import com.example.netpulse.ui.components.TestButton
import com.example.netpulse.ui.formatBytes
import com.example.netpulse.ui.formatMs
import com.example.netpulse.ui.formatPct

@Composable
fun TestScreen(
    state: SpeedTestState,
    samples: List<SpeedSample>,
    animationLevel: String,
    onStop: () -> Unit,
) {
    val intensity = when (animationLevel) { "HIGH" -> 48; "MEDIUM" -> 26; else -> 0 }
    Column(Modifier.fillMaxSize().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("NETWORK TEST", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text("${state.phase.name.lowercase().replace('_', ' ')} · ${(state.progress * 100).toInt()}%", style = MaterialTheme.typography.titleLarge)
        LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))
        Box(Modifier.fillMaxWidth().weight(0.45f)) {
            ParticleField(enabled = intensity > 0, intensity = intensity, modifier = Modifier.fillMaxSize())
            AnimatedContent(targetState = state.currentMbps, label = "testSpeed") {
                SpeedHero(
                    speedMbps = it,
                    phaseLabel = when (state.phase) {
                        TestPhase.PING -> "Latency"
                        TestPhase.DOWNLOAD -> "Download"
                        TestPhase.UPLOAD -> "Upload"
                        TestPhase.ANALYZING -> "Analysis"
                        else -> "Preparing"
                    },
                )
            }
        }
        SpeedGraph(samples = samples)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Ping ${formatMs(state.pingMs)} ms")
            Text("Jitter ${formatMs(state.jitterMs)} ms")
            Text("Loss ${formatPct(state.packetLossPct)}")
        }
        Text("Transferred ${formatBytes(state.transferredBytes)}", modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.secondary)
        TestButton(testing = true, onClick = onStop)
    }
}
