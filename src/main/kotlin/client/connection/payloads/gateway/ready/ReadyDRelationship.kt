package client.connection.payloads.gateway.ready

import client.connection.payloads.gateway.SerialUser

@kotlinx.serialization.Serializable
data class ReadyDRelationship(
    val type : Int,
    val user : SerialUser,
)