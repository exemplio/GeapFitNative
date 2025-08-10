package com.exemplio.geapfitmobile.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class VerifyPasswordResponse(
    val username: String,
    val userId: String,
    val displayName: String,
    val email: String,
    @SerialName("_id") val id: String,
    val createdAt: String
) {
    fun toJson(): String = Json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(json: String): VerifyPasswordResponse =
            Json.decodeFromString(serializer(), json)
    }
}