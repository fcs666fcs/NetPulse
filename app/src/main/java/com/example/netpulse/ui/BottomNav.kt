
package com.example.netpulse.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed class TopLevelDestination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Home : TopLevelDestination("home", "Home", Icons.Outlined.Home)
    data object History : TopLevelDestination("history", "History", Icons.Outlined.History)
    data object Settings : TopLevelDestination("settings", "Settings", Icons.Outlined.Settings)
}

@Composable
fun BottomNav(currentRoute: String, onNavigate: (String) -> Unit) {
    val destinations = listOf(TopLevelDestination.Home, TopLevelDestination.History, TopLevelDestination.Settings)
    NavigationBar {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onNavigate(destination.route) },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
            )
        }
    }
}
