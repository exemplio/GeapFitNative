package com.exemplio.geapfitmobile.utils

import android.content.Context
import android.content.SharedPreferences
import com.exemplio.geapfitmobile.domain.entity.VerifyPasswordResponse
import com.geapfit.network.NetworkState
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CacheService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("geap_fit_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun setOtherCache(name: String, data: String) {
        prefs.edit().putString(name, data).apply()
    }

    fun setCacheJsonFuture(key: String, data: Any) {
        savePrefs(key, data)
    }

    fun setCacheJson(key: String, data: Any) {
        savePrefs(key, data)
    }

    fun getCacheJson(name: String): Any? {
        val value = prefs.getString(name, null) ?: return null
        return gson.fromJson(value, Any::class.java)
    }

    fun getCacheData(name: String): String? {
        return try {
            prefs.getString(name, null)
        } catch (err: Exception) {
            null
        }
    }

    fun getLastCredentials(): Any? {
        return getFromPrefs("last_credentials") {
            gson.fromJson(it, VerifyPasswordResponse::class.java)
        }
    }

    fun deleteCacheData(name: String) {
        try {
            prefs.edit().remove(name).apply()
        } catch (err: Exception) {
            // Log or ignore
        }
    }

    fun emptyCacheData() {
        // Log.i("Eliminando todo en cache")
        try {
            prefs.edit().clear().apply()
        } catch (err: Exception) {
            // Log or ignore
        }
    }

    fun <T> getObj(key: String, f: (Map<String, Any>) -> T): T? {
        val value = getCacheJson(key)
        if (value != null && value is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            return f(value as Map<String, Any>)
        }
        return null
    }

    fun credentialResponse(): VerifyPasswordResponse? {
        return getObj("last_credentials") {
            gson.fromJson(gson.toJson(it), VerifyPasswordResponse::class.java)
        }
    }

    fun areCredentialsStored(): Boolean {
        return credentialResponse() != null
    }

    fun saveNetworkState(networkState: NetworkState) {
        savePrefs("network_state", networkState)
    }

    fun getNetworkState(): NetworkState? {
        return getObj("network_state") {
            gson.fromJson(gson.toJson(it), NetworkState::class.java)
        }
    }

    fun isOnline(): Boolean {
        return getNetworkState()?.isOnline ?: true
    }

    private fun savePrefs(key: String, obj: Any) {
        prefs.edit().putString(key, gson.toJson(obj)).apply()
    }

    private fun getFromPrefs(key: String, parseJson: (String) -> Any?): Any? {
        val json = prefs.getString(key, null) ?: return null
        return parseJson(json)
    }

    fun saveKeepLastSession(keepData: String) {
        savePrefs("keep_session_data", keepData)
    }

    fun getKeepLastSession(): String? {
        return prefs.getString("keep_session_data", null)
    }

    fun saveLastCredentials(credentials: VerifyPasswordResponse) {
        savePrefs("last_credentials", credentials)
    }

    fun getModel(): String? {
        return prefs.getString("device_model", null)?.replace("\"", "")
    }
}