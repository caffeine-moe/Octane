package org.caffeine.octane.entities.channels

import kotlinx.coroutines.runBlocking
import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.guild.Guild
import org.caffeine.octane.typedefs.ChannelType

data class TextChannelImpl(
    override var id : Snowflake,
    override var client : Client,
    override var lastMessageId : Snowflake,
    override var name : String = "",
    override var position : Int = 0,
    override var parentId : Snowflake,
    override var topic : String = "",
    override var nsfw : Boolean = false,
    override val type : ChannelType,
) : TextChannel {

    var guildId : Snowflake = Snowflake(0)

    override var guild : Guild
        get() = runBlocking(client.coroutineContext) { client.user.fetchGuild(guildId) }
        set(value) {
            guildId = value.id
        }
}