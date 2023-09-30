package org.caffeine.octane.entities.channels

import kotlinx.coroutines.runBlocking
import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.guild.Guild
import org.caffeine.octane.typedefs.ChannelType

data class VoiceChannelImpl(
    override val client : Client,
    override val id : Snowflake,
    override val name : String,
    override val position : Int,
    override val lastMessageId : Snowflake,
    override val type : ChannelType
) : VoiceChannel {

    var guildId : Snowflake = Snowflake(0)

    override var guild : Guild
        get() = runBlocking(client.coroutineContext) { client.user.fetchGuild(guildId) }
        set(value) {
            guildId = value.id
        }
}