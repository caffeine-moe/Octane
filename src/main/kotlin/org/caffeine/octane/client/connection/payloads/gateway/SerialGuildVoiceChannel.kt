package org.caffeine.octane.client.connection.payloads.gateway

import kotlinx.serialization.Serializable

@Serializable
data class SerialGuildVoiceChannel(
    val flags : Int = 0,
    val id : String = "",
    val name : String = "",
    val parent_id : String = "",
    val guild_id : String = "",
    val position : Int = 0,
    val type : Int = 0,
    val topic : String = "",
    val last_message_id : String?,
    val bitrate : Int,
    val user_limit : Int
)