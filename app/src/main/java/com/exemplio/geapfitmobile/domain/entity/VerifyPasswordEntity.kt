package com.exemplio.geapfitmobile.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class VerifyPasswordResponse(
    val userName: String,
    val userId: String,
    val roleType: String,
    val email: String,
    val createdAt: String ? = null,
    val _id: String ? = null,
    val __v: Int ? = null,
)