package com.exemplio.geapfitmobile.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class VerifyPasswordResponse(
    val userName: String,
    val userId: String,
    val roleType: String,
    val email: String,
    val createdAt: String ? = null,
)