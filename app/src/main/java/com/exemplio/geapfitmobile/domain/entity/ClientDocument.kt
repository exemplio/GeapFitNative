import com.exemplio.geapfitmobile.domain.entity.UserEntity
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
    val userInfo: UserEntity,
    val displayName: String ? = null,
    val email: String,
    val createdAt: String ? = null,
    val _id: String ? = null,
    val __v: Int ? = null,
)