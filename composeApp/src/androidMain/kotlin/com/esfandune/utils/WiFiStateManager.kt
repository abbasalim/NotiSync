package com.esfandune.utils

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.esfandune.receiver.WiFiStateReceiver

class WiFiStateManager(private val context: Context) {
    private val _isWifiConnected = MutableLiveData<Boolean>(false)
    val isWifiConnected: LiveData<Boolean> = _isWifiConnected

    private val networkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isWifiConnected.postValue(true)
        }

        override fun onLost(network: Network) {
            _isWifiConnected.postValue(false)
        }
    }

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    init {
        // Register network callback for modern connectivity monitoring
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    fun unregisterNetworkCallback() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Ignore if callback was not registered
        }
    }


    private fun updateWiFiState() {
        val network = connectivityManager.activeNetwork ?: run {
            _isWifiConnected.postValue(false)
            return
        }

        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: run {
            _isWifiConnected.postValue(false)
            return
        }

        val isWifiConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        _isWifiConnected.postValue(isWifiConnected)
    }

    @Composable
    fun observeWiFiState(): State<Boolean> {
        val context = LocalContext.current
        val state = remember { mutableStateOf(false) }
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

        // Initial update
        LaunchedEffect(Unit) {
            updateWiFiState()
        }

        // Register broadcast receiver for WiFi state changes
        DisposableEffect(lifecycleOwner) {
            val receiver = WiFiStateReceiver { isConnected ->
                state.value = isConnected
                _isWifiConnected.postValue(isConnected)
            }

            val filter = IntentFilter().apply {
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            }

            context.registerReceiver(receiver, filter)

            onDispose {
                try {
                    context.unregisterReceiver(receiver)
                } catch (e: Exception) {
                    // Receiver was not registered
                }
            }
        }

        // Update state when the composable is first launched
        LaunchedEffect(lifecycleOwner) {
            _isWifiConnected.observe(lifecycleOwner) { isConnected ->
                state.value = isConnected
            }
        }

        return state
    }
}

@Composable
fun rememberWiFiState(): State<Boolean> {
    val context = LocalContext.current
    val wifiStateManager = remember { WiFiStateManager(context) }
    return wifiStateManager.observeWiFiState()
}
