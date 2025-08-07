package com.exemplio.geapfitmobile.data.model

import ErrorResponse

data class Resultado<T>(
    val success: Boolean = false,
    val obj: T? = null,
    val error: Any? = null,
    val stackTrace: Throwable? = null,
    val errorMessage: String? = null,
    val msg: Message? = null,
    val errorResponse: ErrorResponse? = null
) {

    companion object {
        fun <T> fail(error: Any?, stackTrace: Throwable?): Resultado<T> =
            Resultado(
                success = false,
                error = error,
                stackTrace = stackTrace,
                errorMessage = stackTrace?.toString()
            )

        fun <T> failWithErrorMessage(errorMessage: String?, error: Any?, stackTrace: Throwable?): Resultado<T> =
            Resultado(
                success = false,
                error = error,
                stackTrace = stackTrace,
                errorMessage = errorMessage
            )

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

        fun <T> msg(msg: Message, errorMessage: String? = null, success: Boolean = false): Resultado<T> =
            Resultado(
                success = success,
                errorMessage = errorMessage,
                msg = msg
            )

        fun <T> result(result: Resultado<*>): Resultado<T> =
            Resultado(
                success = result.success,
                error = result.error,
                stackTrace = result.stackTrace,
                errorMessage = result.errorMessage,
                msg = result.msg
            )

        fun <S, T> transform(result: Resultado<T>): Resultado<S> =
            Resultado(
                success = result.success,
                error = result.error,
                stackTrace = result.stackTrace,
                errorMessage = result.errorMessage,
                msg = result.msg
            )

        fun <T> identity(resultado: Resultado<T>): Resultado<T> = resultado
    }

    val length: Nothing? = null

    override fun toString(): String {
        return "Resultado(success=$success, obj=$obj, error=$error, stackTrace=$stackTrace, errorMessage=$errorMessage, msg=$msg, errorResponse=$errorResponse)"
    }
}