package client.connection.payloads.gateway

import kotlinx.serialization.Serializable

@Serializable
data class SerialBaseChannel (
    val id : String,
    val type : Int,
)