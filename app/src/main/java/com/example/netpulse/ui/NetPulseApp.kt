
package com.example.netpulse.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.netpulse.core.model.SpeedTestResult
import com.example.netpulse.core.model.TestServer
import com.example.netpulse.ui.screens.HistoryScreen
import com.example.netpulse.ui.screens.HomeScreen
import com.example.netpulse.ui.screens.ResultScreen
import com.example.netpulse.ui.screens.SettingsScreen
import com.example.netpulse.ui.screens.TestScreen

@Composable
fun NetPulseApp(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = viewModel(),
) {
    val state by viewModel.testState.collectAsStateWithLifecycle()
    val samples by viewModel.samples.collectAsStateWithLifecycle()
    val result by viewModel.lastResult.collectAsStateWithLifecycle()
    val selectedHistory by viewModel.selectedHistory.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val navBackStack by navController.currentBackStackEntryAsState()
    val route = navBackStack?.destination?.route ?: "home"

    LaunchedEffect(state.phase) {
        when (state.phase) {
            com.example.netpulse.core.model.TestPhase.COMPLETED -> {
                navController.navigate("result") { launchSingleTop = true }
            }
            com.example.netpulse.core.model.TestPhase.ERROR,
            com.example.netpulse.core.model.TestPhase.CANCELLED -> {
                if (route == "test") navController.popBackStack("home", false)
            }
            else -> Unit
        }
    }

    val bottomVisible = route in setOf("home", "history", "settings")
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (bottomVisible) BottomNav(route, navController::navigate) 
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding),
        ) {
            composable("home") {
                HomeScreen(
                    state = state,
                    samples = samples,
                    demoMode = settings.demoMode,
                    onStart = {
                        viewModel.startTest()
                        navController.navigate("test") { launchSingleTop = true }
                    },
                    onStop = viewModel::stopTest,
                    onSettings = { navController.navigate("settings") },
                )
            }
            composable("test") {
                TestScreen(
                    state = state,
                    samples = samples,
                    animationLevel = settings.animationLevel,
                    onStop = viewModel::stopTest,
                )
            }
            composable("result") {
                val displayResult = selectedHistory?.toSpeedTestResult() ?: result
                ResultScreen(
                    result = displayResult,
                    onRetest = {
                        viewModel.startTest()
                        navController.navigate("test") { launchSingleTop = true }
                    },
                    onHome = {
                        viewModel.reset()
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }
                    },
                )
            }
            composable("history") {
                HistoryScreen(
                    history = history,
                    onSelect = {
                        viewModel.selectHistory(it)
                        navController.navigate("result")
                    },
                    onDelete = viewModel::deleteHistory,
                )
            }
            composable("settings") {
                SettingsScreen(
                    settings = settings,
                    onDuration = viewModel::setDuration,
                    onAnimation = viewModel::setAnimation,
                    onDemoMode = viewModel::setDemoMode,
                    onServerUrl = viewModel::setServerUrl,
                )
            }
        }
    }
}

private fun com.example.netpulse.data.local.HistoryEntity.toSpeedTestResult(): SpeedTestResult =
    SpeedTestResult(
        startedAt = startedAt,
        finishedAt = startedAt + durationMs,
        server = TestServer(serverId, serverName, "—", ""),
        downloadMbps = downloadMbps,
        uploadMbps = uploadMbps,
        pingMs = pingMs,
        jitterMs = jitterMs,
        packetLossPct = lossPct,
        durationMs = durationMs,
        transferredBytes = transferredBytes,
        networkType = networkType,
        ipVersion = ipVersion,
    )
