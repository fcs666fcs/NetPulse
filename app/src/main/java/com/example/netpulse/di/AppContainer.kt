
package com.example.netpulse.di

import android.content.Context
import androidx.room.Room
import com.example.netpulse.core.network.NetworkMonitor
import com.example.netpulse.data.local.NetPulseDatabase
import com.example.netpulse.data.local.SettingsRepository
import com.example.netpulse.data.remote.RemoteServerDataSource
import com.example.netpulse.data.remote.SpeedApi
import com.example.netpulse.data.repository.HistoryRepository
import com.example.netpulse.data.repository.ServerRepository
import com.example.netpulse.domain.speedtest.DefaultSpeedTestEngine
import com.example.netpulse.domain.speedtest.DemoSpeedTestEngine
import com.example.netpulse.domain.speedtest.DownloadTester
import com.example.netpulse.domain.speedtest.PingTester
import com.example.netpulse.domain.speedtest.SpeedTestEngine
import com.example.netpulse.domain.speedtest.UploadTester
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val settingsRepository = SettingsRepository(appContext)
    val database: NetPulseDatabase = Room.databaseBuilder(
        appContext,
        NetPulseDatabase::class.java,
        "netpulse.db",
    ).build()
    val historyRepository = HistoryRepository(database.historyDao())
    val networkMonitor = NetworkMonitor(appContext)

    private val okHttp = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://speed.example.com/")
        .client(okHttp)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val apiFactory: (String) -> SpeedApi = { base ->
        Retrofit.Builder()
            .baseUrl(if (base.endsWith('/')) base else "$base/")
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpeedApi::class.java)
    }

    private val remoteServerDataSource = RemoteServerDataSource(apiFactory)
    private val serverRepository = ServerRepository(remoteServerDataSource)
    private val pingTester = PingTester(okHttp)
    private val downloadTester = DownloadTester(okHttp)
    private val uploadTester = UploadTester(okHttp)

    fun createEngine(settings: com.example.netpulse.data.local.AppSettings): SpeedTestEngine =
        if (settings.demoMode) {
            DemoSpeedTestEngine()
        } else {
            DefaultSpeedTestEngine(
                serverRepository = serverRepository,
                pingTester = pingTester,
                downloadTester = downloadTester,
                uploadTester = uploadTester,
                networkMonitor = networkMonitor,
                configuredApiUrl = settings.serverListUrl,
            )
        }
}
