package org.caffeine.octane.entities.channels

import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.typedefs.ChannelType

interface BaseChannel {
    suspend fun delete()

    val client : Client
    val id : Snowflake
    val name : String
    val type : ChannelType
}