package org.caffeine.octane.client.connection.payloads.gateway.ready

import org.caffeine.octane.client.connection.payloads.gateway.SerialUser

@kotlinx.serialization.Serializable
data class ReadyDRelationship(
    val type : Int,
    val user : SerialUser,
)