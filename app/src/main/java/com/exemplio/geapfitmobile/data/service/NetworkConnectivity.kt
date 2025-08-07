package com.geapfit.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.exemplio.geapfitmobile.utils.MyUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.net.InetAddress

class NetworkConnectivity private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: NetworkConnectivity? = null

        fun getInstance(context: Context): NetworkConnectivity {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkConnectivity(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val TAG = "NetworkConnectivity"
    private val _networkStateFlow = MutableSharedFlow<NetworkState>(replay = 1)
    val networkStateFlow: SharedFlow<NetworkState> get() = _networkStateFlow

    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var job: Job? = null

    fun initialise() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            val result = getConnectivityResult()
            checkStatus(result)
        }
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = getConnectivityResult()
                    checkStatus(result)
                }
            }

            override fun onLost(network: android.net.Network) {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = getConnectivityResult()
                    checkStatus(result)
                }
            }
        }
        val request = android.net.NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback!!)
    }

    private suspend fun checkStatus(result: ConnectivityResult) {
        var isOnline = false
        val beforeLookup = System.currentTimeMillis()

        if (result != ConnectivityResult.NONE) {
            try {
                val address = withContext(Dispatchers.IO) {
                    InetAddress.getByName(MyUtils.base)
                }
                isOnline = address.hostAddress.isNotEmpty()
            } catch (e: Exception) {
                isOnline = false
            }
        }

        val afterLookup = System.currentTimeMillis()
        val difference = afterLookup - beforeLookup
        Log.d(TAG, "Lookup duration ${difference}ms RESULT $result $isOnline")
        _networkStateFlow.tryEmit(NetworkState(result, isOnline))
    }

    fun close() {
        Log.i(TAG, "CLOSE")
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        job?.cancel()
    }

    private fun getConnectivityResult(): ConnectivityResult {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return when {
            capabilities == null -> ConnectivityResult.NONE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectivityResult.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectivityResult.MOBILE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectivityResult.ETHERNET
            else -> ConnectivityResult.UNKNOWN
        }
    }
}

enum class ConnectivityResult {
    WIFI,
    MOBILE,
    ETHERNET,
    NONE,
    UNKNOWN
}

data class NetworkState(
    @SerializedName("result") val result: ConnectivityResult,
    @SerializedName("is_online") val isOnline: Boolean
) {
    fun toJson(): String = Gson().toJson(this)

    companion object {
        fun fromJson(json: String): NetworkState = Gson().fromJson(json, NetworkState::class.java)
    }
}
