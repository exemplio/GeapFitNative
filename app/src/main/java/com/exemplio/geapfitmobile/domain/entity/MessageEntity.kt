import com.exemplio.geapfitmobile.domain.entity.VerifyPasswordResponse
import kotlinx.serialization.Serializable

@Serializable
data class ReceiveMessageModel(
    val content: String?,
    val type: String? = null,
    val userName: String? = null,
    val receiverId: String? = null,
    val senderId: String? = null,
    val chatId: ChatInfo? = null,
    val readBy: List<String> = emptyList(),
    val createdAt: String? = null,
    var isFromMe: Boolean? = null,
    val _id: String ? = null,
    val __v: Int ? = null,
)

@Serializable
data class ChatInfo(
    val displayName: String ? = null,
    val createdAt: String ? = null,
    val initials: String ? = null,
    val name: String ? = null,
    val date: String ? = null,
    val lastMessage: String ? = null,
    val chatId: String ? = null,
    val isGroup: Boolean ? = null,
    val members: List<VerifyPasswordResponse> ? = null,
    val _id: String ? = null,
    val __v: Int ? = null,
)

@Serializable
data class SendMessageModel(
    val content: String?,
    val type: String? = null,
    val userName: String? = null,
    val receiverId: String?,
    val senderId: String?,
    val chatId: String?,
    val readBy: List<String> = emptyList(),
    val createdAt: String? = null,
)

@Serializable
data class MessageWss(
    val message: ReceiveMessageModel
)