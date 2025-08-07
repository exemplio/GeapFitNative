package com.exemplio.geapfitmobile.utils

import java.util.*
import kotlin.math.roundToInt
import android.content.Context
import android.content.SharedPreferences
import com.exemplio.geapfitmobile.BuildConfig
import kotlinx.coroutines.withContext

object MyUtils {
    val random: Random = Random()

    val nonAuthServices: Set<String> = setOf(
        "/oauth/authorize",
        "/oauth/info_from_credentials"
    )
    val operador: MutableMap<String, String> = mutableMapOf()
    val operadorNumber: MutableMap<String, String> = mutableMapOf()

    fun parseDNI(dni: String): String {
        return dni.padStart(9, '0')
    }

    fun isNumeric(s: String?): Boolean {
        if (s == null) return false
        return s.toDoubleOrNull() != null
    }

    fun parseAmount(amount: Any?): Double? {
        return when (amount) {
            is String -> amount.toDoubleOrNull()?.takeIf { it.isFinite() }
            is Int -> amount.toDouble()
            is Double -> amount
            else -> null
        }
    }

    fun parseAmountInt(amount: Any?): Int? {
        return when (amount) {
            is String -> amount.toIntOrNull()?.takeIf { true }
            is Int -> amount
            is Double -> amount.roundToInt()
            else -> null
        }
    }

    val authId = BuildConfig.AUTH_ID
    val type = BuildConfig.CONTEXT_PATH
    val typeAuth = BuildConfig.CONTEXT_AUTH_PATH
    val clientId = BuildConfig.CLIENT_ID
    val base: String
        get() = BuildConfig.API_URL

    val baseAuth: String
        get() = BuildConfig.API_AUTH_URL

    val apiKey: String
        get() = BuildConfig.API_KEY

    var params: MutableMap<String, String> = mutableMapOf()
    var params2: MutableMap<String, String> = mutableMapOf()

    val headers: MutableMap<String, String> = mutableMapOf(
        "Content-type" to "application/json",
        "Accept" to "application/json"
    )
    val headers2: MutableMap<String, String> = mutableMapOf(
        "Content-type" to "application/x-www-form-urlencoded"
    )

    fun cryptoKey(): String = "DixFbJ8hts9YNyHEIYFIh6J1ZHJLAMUlKCkCBtjpvyM="

    fun cryptoIV(): String = "Ra5z/FEYQfDgIFcqSxIZSw=="

    suspend fun prefs(context: Context): SharedPreferences {
        return withContext(kotlinx.coroutines.Dispatchers.IO) {
            context.getSharedPreferences("default_prefs", Context.MODE_PRIVATE)
        }
    }
}