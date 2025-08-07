package com.exemplio.geapfitmobile.data.repository

import com.exemplio.geapfitmobile.data.service.HttpServiceImpl
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
import com.exemplio.geapfitmobile.data.service.IsOnlineProvider
import com.exemplio.geapfitmobile.domain.entity.UserEntity
import com.exemplio.geapfitmobile.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    api: ApiServicesImpl,
    private val httpService: HttpServiceImpl,
    private val isOnlineProvider: IsOnlineProvider
) : AuthRepository {
    override suspend fun doLogin(user: String, password: String): List<UserEntity> {
        TODO("Not yet implemented")
    }


}
