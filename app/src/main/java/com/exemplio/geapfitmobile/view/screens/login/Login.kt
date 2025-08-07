package com.exemplio.geapfitmobile.view.auth.login

import ClientDocument
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
import com.exemplio.geapfitmobile.domain.entity.PasswordGrantEntity
import com.exemplio.geapfitmobile.data.model.Resultado
import com.exemplio.geapfitmobile.domain.entity.VerifyPasswordResponse
import javax.inject.Inject

class Login @Inject constructor(private val apiService: ApiServicesImpl
) {
    suspend fun login(user: String, password: String): Resultado<VerifyPasswordResponse?>? {
        if (user.contains("@hotmail.com")) {
            return null
        }
        val body = PasswordGrantEntity(
            email = user,
            password = password,
        )
        return apiService.passwordGrant(body)
    }
}