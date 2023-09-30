package entities.channels

import entities.guild.Guild

interface GuildChannel : BaseChannel {
    val guild : Guild
    val position : Int
}