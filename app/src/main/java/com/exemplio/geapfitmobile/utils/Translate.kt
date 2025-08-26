package com.geapfit.utils

fun translate(message: String?): String {
    return when (message?.uppercase()) {
        "INVALID_LOGIN_CREDENTIALS" ->
            "Credenciales de inicio de sesión inválidas"
        "EMAIL_EXISTS" ->
            "El correo electrónico ya está en uso"
        else -> message ?: ""
    }
}