import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReceive(
    val chat: String,
    val sender: String? = null,
    val content: String,
    val readBy: List<String> = emptyList(),
    val createdAt: String
)