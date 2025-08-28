package com.exemplio.geapfitmobile.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val userName: String ? = null,
    val _id: String ? = null,
    val roleType: String ? = null,
    val email: String ? = null,
    val createdAt: String ? = null,
    val __v: Int ? = null,
)