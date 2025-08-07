package com.geapfit.utils

fun translate(message: String?): String {
    return when (message?.uppercase()) {
        "INVALID_LOGIN_CREDENTIALS" ->
            "Credenciales de inicio de sesión inválidas"
        else -> message ?: ""
    }
}