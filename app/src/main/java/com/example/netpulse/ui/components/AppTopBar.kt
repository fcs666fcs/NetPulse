
package com.example.netpulse.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AppTopBar(title: String, onSettings: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        actions = {
            onSettings?.let {
                IconButton(onClick = it) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                }
            }
        },
    )
}
