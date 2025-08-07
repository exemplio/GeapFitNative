import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientsResponse(
    val documents: List<ClientDocument>
)

@Serializable
data class ClientDocument(
    val name: String,
    val fields: ClientFields,
    val createTime: String,
    val updateTime: String
)

@Serializable
data class ClientFields(
    val id: StringValue,
    val status: StringValue,
    val initials: StringValue,
    val updateTime: StringValue,
    val lastActivity: StringValue,
    val createTime: StringValue,
    val name: StringValue
)

@Serializable
data class StringValue(
    val stringValue: String
)