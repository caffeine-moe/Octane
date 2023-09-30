package org.caffeine.octane.entities.channels

import org.caffeine.octane.entities.guild.Guild

interface GuildChannel : BaseChannel {
    val guild : Guild
    val position : Int
}