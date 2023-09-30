package org.caffeine.octane.entities.channels

import org.caffeine.octane.entities.Snowflake

interface GuildTextChannel : TextCapableChannel, GuildChannel {
    val parentId : Snowflake
    val topic : String
    val nsfw : Boolean
}