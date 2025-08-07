package com.exemplio.geapfitmobile.data.repository

import ClientRepository
import android.util.Log
import com.exemplio.geapfitmobile.domain.entity.UserEntity
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor() : ClientRepository {
    override suspend fun getClients(): List<UserEntity> {
        TODO("Not yet implemented")
    }


}
