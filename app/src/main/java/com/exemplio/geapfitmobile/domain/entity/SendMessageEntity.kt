import kotlinx.serialization.Serializable

@Serializable
data class SendMessage(
    val content: String?,
    val type: String,
    val userName: String,
    val receiverId: String?,
    val senderId: String?,
    val chatId: String?
)