
package com.example.netpulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.netpulse.ui.NetPulseApp
import com.example.netpulse.ui.theme.NetPulseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetPulseTheme {
                NetPulseApp()
            }
        }
    }
}
