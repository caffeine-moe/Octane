package client.connection.payloads.gateway.init

import kotlinx.serialization.Serializable

@Serializable
data class Init(
    val heartbeat_interval : Long,
    val _trace : List<String>,
)