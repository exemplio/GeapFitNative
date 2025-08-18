package com.exemplio.geapfitmobile.data.model

import ErrorResponse

data class Resultado<T>(
    val success: Boolean = false,
    val obj: T? = null,
    val error: Any? = null,
    val stackTrace: Throwable? = null,
    val errorMessage: String? = null,
    val errorResponse: ErrorResponse? = null
) {

    companion object {

        fun <T> failMsg(errorMessage: String?, error: Int?): Resultado<T> =
            Resultado(
                success = false,
                errorMessage = errorMessage,
                error = error,
            )

        fun <T> success(obj: T?): Resultado<T> =
            Resultado(
                success = true,
                obj = obj
            )

        fun <T> identity(resultado: Resultado<T>): Resultado<T> = resultado
    }

    val length: Nothing? = null

    override fun toString(): String {
        return "Resultado(success=$success, obj=$obj, error=$error, stackTrace=$stackTrace, errorMessage=$errorMessage, errorResponse=$errorResponse)"
    }
}