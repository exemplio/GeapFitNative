package com.exemplio.geapfitmobile.domain.repository

import com.exemplio.geapfitmobile.domain.entity.UserEntity

interface AuthRepository {
    suspend fun doLogin(user:String, password:String):List<UserEntity>
}