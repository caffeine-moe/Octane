package client.connection.payloads.gateway

import kotlinx.serialization.Serializable

@Serializable
data class SerialTextBasedChannel(
    val type : Int,
    val id : String,
    val name : String = "",
    val last_message_id : String,
)