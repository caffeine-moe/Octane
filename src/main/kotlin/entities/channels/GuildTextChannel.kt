package entities.channels

import entities.Snowflake

interface GuildTextChannel : TextCapableChannel, GuildChannel {
    val parentId : Snowflake
    val topic : String
    val nsfw : Boolean
}