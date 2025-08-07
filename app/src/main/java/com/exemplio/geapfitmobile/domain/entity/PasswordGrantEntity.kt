package com.exemplio.geapfitmobile.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PasswordGrantEntity(
    val email: String,
    val password: String,
)