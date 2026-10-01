
package com.example.netpulse.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MetricCard(title: String, value: String, subtitle: String? = null) {
    Card(
        modifier = Modifier.width(104.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
            Text(value, fontSize = 23.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
            subtitle?.let { Text(it, fontSize = 9.sp, color = MaterialTheme.colorScheme.secondary) }
        }
    }
}
