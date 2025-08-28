import com.exemplio.geapfitmobile.domain.entity.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class ChatItem(
    val displayName: String ? = null,
    val createdAt: String ? = null,
    val initials: String ? = null,
    val name: String ? = null,
    val date: String ? = null,
    val lastMessage: String ? = null,
    val chatId: String ? = null,
    val isGroup: Boolean ? = null,
    val members: UserEntity ? = null,
    val _id: String ? = null,
    val __v: Int ? = null,
)