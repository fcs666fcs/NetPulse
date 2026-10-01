
package com.example.netpulse

import android.app.Application
import com.example.netpulse.di.AppContainer

class NetPulseApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
