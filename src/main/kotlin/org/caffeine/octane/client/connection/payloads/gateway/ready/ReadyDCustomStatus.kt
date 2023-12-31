package org.caffeine.octane.client.connection.payloads.gateway.ready

@kotlinx.serialization.Serializable
data class ReadyDCustomStatus(
    val emoji_id : String? = "",
    val emoji_name : String? = "",
    val expires_at : String? = "",
    val text : String = "",
)