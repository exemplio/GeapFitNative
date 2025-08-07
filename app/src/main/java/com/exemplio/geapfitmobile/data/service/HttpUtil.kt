import com.exemplio.geapfitmobile.data.model.Message
import com.exemplio.geapfitmobile.data.model.Resultado
import com.geapfit.utils.translate
import okhttp3.Response


object HttpUtil {

    fun <T> result(
        response: Response,
        error: ErrorResponse? = null,
        parsedJson: T? = null,
    ): Resultado<T?> {
        val jsonElement = response.body
        return when (response.code) {
            200 -> {
                println("HTTP response: $response")
                if (parsedJson != null) {
                    Resultado.success(parsedJson)
                } else {
                    Resultado.success(null)
                }
            }

            202, 422 -> {
                println("Codigo de respuesta: ${response.code}")
                val message = Message(jsonElement.toString())
                val errorMsg = error?.error
                var error = ""
                if (errorMsg != null) {
                    error += translate(errorMsg.message)
                }
                if (error.isEmpty()) {
                    error = "EMPTY_ERROR"
                }
                Resultado.msg<T?>(message, errorMessage = error)
            }

            400 -> {
                Resultado.failMsg<T?>(errorMessage = error?.error?.message.toString(), error = response.code)
            }
            401 -> {
                Resultado.failMsg<T?>("Error de autenticación", error = response.code)
            }
            403 -> {
                Resultado.failMsg<T?>("No hay permisos para acceder a este recurso", error = response.code)
            }
            502 -> {
                Resultado.failMsg<T?>("Gateway timeout", error = response.code)
            }
            503 -> {
                Resultado.failMsg<T?>("Service Unavailable",error = response.code,)
            }
            else -> Resultado.failMsg<T?>(errorMessage = "${response.code} ${response.body?.string()}", error = response.code)
        }
    }
}