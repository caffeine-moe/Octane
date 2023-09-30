package client.connection.payloads.gateway

@kotlinx.serialization.Serializable
data class SerialPrivateChannel(
    val id : String = "",
    val last_message_id : String = "",
    val recipients : List<SerialUser> = emptyList(),
    val name : String = "",
    val type : Int = 0,
)