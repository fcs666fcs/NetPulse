package com.example.netpulse.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
    ) {
        Text(
            text = "RESULT",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "测试完成",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(
            modifier = Modifier.height(6.dp),
        )

        if (result == null) {
            Text("暂无测试结果")
            return@Column
        }

        SpeedHero(
            speed = result.downloadMbps,
            label = "Download",
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MetricCard(
                title = "UPLOAD",
                value = formatSpeed(result.uploadMbps),
                unit = "Mbps",
            )

            MetricCard(
                title = "PING",
                value = formatMs(result.pingMs),
                unit = "ms",
            )

            MetricCard(
                title = "LOSS",
                value = formatPct(result.packetLossPct),
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp),
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Detailed metrics",
                    style = MaterialTheme.typography.titleMedium,
                )

                Detail(
                    "Download average",
                    "${formatSpeed(result.downloadMbps)} Mbps",
                )

                Detail(
                    "Download peak",
                    "${formatSpeed(result.downloadPeakMbps)} Mbps",
                )

                Detail(
                    "Upload average",
                    "${formatSpeed(result.uploadMbps)} Mbps",
                )

                Detail(
                    "Upload peak",
                    "${formatSpeed(result.uploadPeakMbps)} Mbps",
                )

                Detail(
                    "Ping",
                    "${formatMs(result.pingMs)} ms",
                )

                Detail(
                    "Jitter",
                    "${formatMs(result.jitterMs)} ms",
                )

                Detail(
                    "Duration",
                    "${result.durationMs / 1000.0} s",
                )

                Detail(
                    "Transferred",
                    formatBytes(result.transferredBytes),
                )

                Detail(
                    "Network",
                    result.networkType ?: "—",
                )

                Detail(
                    "Server",
                    result.server?.name ?: "—",
                )

                Detail(
                    "Time",
                    formatDate(result.startedAt),
                )
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = onRetest,
                modifier = Modifier.weight(1f),
            ) {
                Text("RETEST")
            }

            OutlinedButton(
                onClick = {
                    val shareText =
                        "NetPulse\n" +
                            "Download ${formatSpeed(result.downloadMbps)} Mbps · " +
                            "Upload ${formatSpeed(result.uploadMbps)} Mbps · " +
                            "Ping ${formatMs(result.pingMs)} ms · " +
                            "Jitter ${formatMs(result.jitterMs)} ms · " +
                            "Loss ${formatPct(result.packetLossPct)}"

                    val intent = Intent(
                        Intent.ACTION_SEND,
                    ).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            shareText,
                        )
                    }

                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            "Share result",
                        ),
                    )
                },
                modifier = Modifier.weight(1f),
            ) {
                Text("SHARE")
            }
        }

        OutlinedButton(
            onClick = onHome,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Text("BACK HOME")
        }
    }
}

@Composable
private fun Detail(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.secondary,
        )

        Text(value)
    }
}
