package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Monitors device network connectivity state using ConnectivityManager.NetworkCallback.
 * Supports auto-detection of network changes and provides manual override for low-connectivity testing.
 */
class NetworkConnectivityMonitor(
    private val context: Context,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isNetworkAvailable = MutableStateFlow(checkInitialConnectivity())
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isSimulated: Boolean = false

    init {
        registerNetworkCallback()
    }

    private fun checkInitialConnectivity(): Boolean {
        return try {
            val cm = connectivityManager ?: return false
            val activeNetwork = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } catch (e: Exception) {
            // Default to true or fallback gracefully
            false
        }
    }

    private fun registerNetworkCallback() {
        try {
            val cm = connectivityManager ?: return
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    if (!isSimulated) {
                        externalScope.launch {
                            _isNetworkAvailable.value = true
                        }
                    }
                }

                override fun onLost(network: Network) {
                    if (!isSimulated) {
                        externalScope.launch {
                            _isNetworkAvailable.value = checkInitialConnectivity()
                        }
                    }
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    if (!isSimulated) {
                        val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        externalScope.launch {
                            _isNetworkAvailable.value = hasInternet
                        }
                    }
                }
            }

            networkCallback = callback
            cm.registerNetworkCallback(request, callback)
        } catch (e: Exception) {
            // In robolectric or environments without active network service, handle gracefully
        }
    }

    /**
     * Manually simulates network changes for testing and UI demonstrations
     */
    fun simulateNetworkAvailability(available: Boolean) {
        isSimulated = true
        _isNetworkAvailable.value = available
    }

    /**
     * Resets simulated state and re-checks real network connectivity
     */
    fun resetSimulation() {
        isSimulated = false
        _isNetworkAvailable.value = checkInitialConnectivity()
    }

    fun unregister() {
        try {
            networkCallback?.let { connectivityManager?.unregisterNetworkCallback(it) }
            networkCallback = null
        } catch (e: Exception) {
            // ignored
        }
    }
}
