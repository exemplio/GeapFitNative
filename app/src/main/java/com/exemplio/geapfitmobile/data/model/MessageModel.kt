import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    val chat: String,
    val sender: String? = null,
    val receiver: String? = null,
    val content: String,
    val readBy: List<String> = emptyList(),
    val createdAt: String,
)