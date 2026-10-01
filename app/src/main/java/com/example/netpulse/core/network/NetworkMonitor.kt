
package com.example.netpulse.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface

class NetworkMonitor(context: Context) {
    private val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun snapshot(): NetworkSnapshot {
        val network = connectivity.activeNetwork
        val capabilities = network?.let(connectivity::getNetworkCapabilities)
        val connected = capabilities != null
        val transport = when {
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi"
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular"
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
            else -> "Offline"
        }
        val label = when {
            !connected -> "No network"
            transport == "Wi-Fi" -> "Wi-Fi · Connected"
            else -> "$transport · Connected"
        }
        val addresses = NetworkInterface.getNetworkInterfaces()?.toList().orEmpty()
            .flatMap { it.inetAddresses.toList() }
            .filterNot { it.isLoopbackAddress || it.isLinkLocalAddress }
        val ipVersion = when {
            addresses.any { it is Inet6Address } -> "IPv6"
            addresses.any { it is Inet4Address } -> "IPv4"
            else -> "—"
        }
        return NetworkSnapshot(connected, transport, label, ipVersion)
    }
}
