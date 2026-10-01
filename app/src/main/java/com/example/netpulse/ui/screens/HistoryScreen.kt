
package com.example.netpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.netpulse.data.local.HistoryEntity
import com.example.netpulse.ui.formatDate
import com.example.netpulse.ui.formatMs
import com.example.netpulse.ui.formatSpeed

@Composable
fun HistoryScreen(
    history: List<HistoryEntity>,
    onSelect: (HistoryEntity) -> Unit,
    onDelete: (Long) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Text("History", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
        Text("最近测速记录", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(bottom = 12.dp))
        if (history.isEmpty()) {
            Text("还没有测速记录。完成一次测试后会显示在这里。", modifier = Modifier.padding(top = 30.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(history, key = { it.id }) { item ->
                    Card(
                        onClick = { onSelect(item) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.serverName, style = MaterialTheme.typography.titleMedium)
                                Text(formatDate(item.startedAt), style = MaterialTheme.typography.labelSmall)
                            }
                            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("↓ ${formatSpeed(item.downloadMbps)} Mbps")
                                Text("↑ ${formatSpeed(item.uploadMbps)} Mbps")
                                Text("${formatMs(item.pingMs)} ms")
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { onDelete(item.id) }) { Text("DELETE") }
                            }
                        }
                    }
                }
            }
        }
    }
}
