import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val content: String,
    val type: String,
    val username: String,
    val receiver: String,
    val sender: String,
    val chat: String
)