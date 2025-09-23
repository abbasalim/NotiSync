package com.esfandune.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager

class WiFiStateReceiver(
    private val onWiFiStateChanged: (Boolean) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            WifiManager.NETWORK_STATE_CHANGED_ACTION,
            WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                updateWiFiState(context)
            }
        }
    }

    private fun updateWiFiState(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: run {
            onWiFiStateChanged(false)
            return
        }
        
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: run {
            onWiFiStateChanged(false)
            return
        }
        
        val isWifiConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        onWiFiStateChanged(isWifiConnected)
    }
}
