import kotlinx.serialization.Serializable

@Serializable
data class ReceiveMessageModel(
    val content: String?,
    val type: String? = null,
    val userName: String? = null,
    val receiverId: String?,
    val senderId: String?,
    val chatId: ChatItem?,
    val readBy: List<String> = emptyList(),
    val createdAt: String? = null,
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