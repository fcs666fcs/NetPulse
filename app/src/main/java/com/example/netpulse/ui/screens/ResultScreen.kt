
package com.example.netpulse.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.netpulse.core.model.SpeedTestResult
import com.example.netpulse.ui.components.MetricCard
import com.example.netpulse.ui.components.SpeedHero
import com.example.netpulse.ui.formatBytes
import com.example.netpulse.ui.formatDate
import com.example.netpulse.ui.formatMs
import com.example.netpulse.ui.formatPct
import com.example.netpulse.ui.formatSpeed

@Composable
fun ResultScreen(
    result: SpeedTestResult?,
    onRetest: () -> Unit,
    onHome: () -> Unit,
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Text("RESULT", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text("测试完成", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.padding(6.dp))
        if (result == null) {
            Text("暂无测试结果")
            return@Column
        }
        SpeedHero(result.downloadMbps, "Download")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricCard("UPLOAD", formatSpeed(result.uploadMbps), "Mbps")
            MetricCard("PING", formatMs(result.pingMs), "ms")
            MetricCard("LOSS", formatPct(result.packetLossPct))
        }
        Spacer(Modifier.padding(6.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
            Column(Modifier.padding(16.dp)) {
                Text("Detailed metrics", style = MaterialTheme.typography.titleMedium)
                Detail("Download average", "${formatSpeed(result.downloadMbps)} Mbps")
                Detail("Download peak", "${formatSpeed(result.downloadPeakMbps)} Mbps")
                Detail("Upload average", "${formatSpeed(result.uploadMbps)} Mbps")
                Detail("Upload peak", "${formatSpeed(result.uploadPeakMbps)} Mbps")
                Detail("Ping", "${formatMs(result.pingMs)} ms")
                Detail("Jitter", "${formatMs(result.jitterMs)} ms")
                Detail("Duration", "${result.durationMs / 1000.0} s")
                Detail("Transferred", formatBytes(result.transferredBytes))
                Detail("Network", result.networkType ?: "—")
                Detail("Server", result.server?.name ?: "—")
                Detail("Time", formatDate(result.startedAt))
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onRetest, modifier = Modifier.weight(1f)) { Text("RETEST") }
            OutlinedButton(
                onClick = {
                    val text = "NetPulse\nDownload ${formatSpeed(result.downloadMbps)} Mbps · Upload ${formatSpeed(result.uploadMbps)} Mbps · Ping ${formatMs(result.pingMs)} ms · Jitter ${formatMs(result.jitterMs)} ms · Loss ${formatPct(result.packetLossPct)}"
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }, "Share result"))
                },
                modifier = Modifier.weight(1f),
            ) { Text("SHARE") }
        }
        OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("BACK HOME") }
    }
}

@Composable
private fun Detail(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.secondary)
        Text(value)
    }
}
