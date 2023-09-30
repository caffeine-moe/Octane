package entities.channels

import client.Client
import entities.Snowflake
import typedefs.ChannelType

interface BaseChannel {
    suspend fun delete()

    val client : Client
    val id : Snowflake
    val name : String
    val type : ChannelType
}