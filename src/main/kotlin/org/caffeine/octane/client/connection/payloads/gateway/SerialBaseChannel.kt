package org.caffeine.octane.client.connection.payloads.gateway

import kotlinx.serialization.Serializable

@Serializable
data class SerialBaseChannel (
    val id : String,
    val type : Int,
)