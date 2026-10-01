
package com.example.netpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.netpulse.data.local.AppSettings

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onDuration: (Int) -> Unit,
    onAnimation: (String) -> Unit,
    onDemoMode: (Boolean) -> Unit,
    onServerUrl: (String) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Text("本地设置，使用 DataStore 保存。", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 4.dp, bottom = 18.dp))

        Text("Test duration · ${settings.durationSeconds}s", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = settings.durationSeconds.toFloat(),
            onValueChange = { onDuration(it.toInt()) },
            valueRange = 5f..30f,
            steps = 24,
        )

        HorizontalDivider(Modifier.padding(vertical = 12.dp))
        Text("Animation level", style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(top = 10.dp)) {
            listOf("HIGH", "MEDIUM", "LOW").forEachIndexed { index, level ->
                SegmentedButton(
                    selected = settings.animationLevel == level,
                    onClick = { onAnimation(level) },
                    shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(index, 3),
                ) { Text(level) }
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("Demo mode", style = MaterialTheme.typography.titleMedium)
                Text("不连接真实测速节点，使用本地模拟数据验证 UI/动画。", color = MaterialTheme.colorScheme.secondary)
            }
            Switch(checked = settings.demoMode, onCheckedChange = onDemoMode)
        }

        OutlinedTextField(
            value = settings.serverListUrl,
            onValueChange = onServerUrl,
            enabled = !settings.demoMode,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            label = { Text("Server API base URL") },
            supportingText = { Text("真实模式要求 /api/v1/servers 及测速 endpoints 可用。") },
            singleLine = true,
        )
    }
}
