package com.exemplio.geapfitmobile.domain.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RegisterEntity(
    val email: String,
    val password: String,
    val roleType: RoleType,
    val userName: String,
)

enum class RoleType {
    STANDARD,
    ADMIN,
}