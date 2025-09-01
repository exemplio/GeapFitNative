import com.exemplio.geapfitmobile.data.model.Resultado
import com.geapfit.utils.translate
import okhttp3.Response


object HttpUtil {

    fun <T> result(
        response: Response,
        error: ErrorResponse? = null,
        parsedJson: T? = null,
    ): Resultado<T?> {
        return when (response.code) {
            200 -> {
                if (parsedJson != null) {
                    Resultado.success(parsedJson)
                } else {
                    Resultado.success(null)
                }
            }

            202, 422 -> {
                val errorMsg = error?.error
                var error = ""
                if (errorMsg != null) {
                    error += translate(errorMsg.message)
                }
                if (error.isEmpty()) {
                    error = "EMPTY_ERROR"
                }
                Resultado.failMsg<T?>(errorMessage = error, error = response.code)
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