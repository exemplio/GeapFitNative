import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReceive(
    val chatId: String,
    val senderId: String? = null,
    val content: String,
    val readBy: List<String> = emptyList(),
    val createdAt: String,
    val receiverId: String? = null,
)

@Serializable
data class MessageWss(
    val message: MessageReceive
)