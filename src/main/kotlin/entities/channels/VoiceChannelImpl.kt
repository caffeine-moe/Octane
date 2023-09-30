package entities.channels

import kotlinx.coroutines.runBlocking
import client.Client
import entities.Snowflake
import entities.guild.Guild
import typedefs.ChannelType

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