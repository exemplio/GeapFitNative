package com.exemplio.geapfitmobile.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class VerifyPasswordResponse(
    val userName: String ? = null,
    val userId: String ? = null,
    val roleType: String ? = null,
    val email: String ? = null,
    val createdAt: String ? = null,
    val _id: String ? = null,
    val __v: Int ? = null,
)