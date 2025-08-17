import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientsResponse(
    val success: Boolean,
    val data: List<Client>,
    val pagination: Pagination
)

@Serializable
data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val pages: Int
)

@Serializable
data class Client(
    val userName: String,
    val userId: String,
    val displayName: String ? = null,
    val email: String,
    val createdAt: String ? = null,
)