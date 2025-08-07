import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: ErrorDetail,
)

@Serializable
data class ErrorDetail(
    val code: Int,
    val message: String,
    val errors: List<ErrorItem>
)

@Serializable
data class ErrorItem(
    val message: String,
    val domain: String,
    val reason: String
)