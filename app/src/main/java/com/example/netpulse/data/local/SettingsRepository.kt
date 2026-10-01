
package com.example.netpulse.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore("settings")

data class AppSettings(
    val durationSeconds: Int = 10,
    val preferredServerId: String? = null,
    val animationLevel: String = "HIGH",
    val demoMode: Boolean = true,
    val serverListUrl: String = "https://speed.example.com/",
)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val duration = intPreferencesKey("duration_seconds")
        val preferredServer = stringPreferencesKey("preferred_server")
        val animation = stringPreferencesKey("animation_level")
        val demoMode = booleanPreferencesKey("demo_mode")
        val serverUrl = stringPreferencesKey("server_list_url")
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { p ->
        AppSettings(
            durationSeconds = p[Keys.duration] ?: 10,
            preferredServerId = p[Keys.preferredServer],
            animationLevel = p[Keys.animation] ?: "HIGH",
            demoMode = p[Keys.demoMode] ?: true,
            serverListUrl = p[Keys.serverUrl] ?: "https://speed.example.com/",
        )
    }

    suspend fun setDuration(value: Int) = edit { it[Keys.duration] = value.coerceIn(5, 30) }
    suspend fun setPreferredServer(value: String?) = edit {
        if (value == null) it.remove(Keys.preferredServer) else it[Keys.preferredServer] = value
    }
    suspend fun setAnimation(value: String) = edit { it[Keys.animation] = value }
    suspend fun setDemoMode(value: Boolean) = edit { it[Keys.demoMode] = value }
    suspend fun setServerUrl(value: String) = edit { it[Keys.serverUrl] = value }

    private suspend fun edit(block: suspend (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        context.settingsDataStore.edit(block)
    }
}
