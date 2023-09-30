package entities.channels

import kotlinx.coroutines.runBlocking
import client.Client
import entities.Snowflake
import entities.guild.Guild
import typedefs.ChannelType

data class ChannelCategoryImpl(
    override val client : Client,
    override val id : Snowflake,
    override val name : String,
    override val position : Int,
    override val type : ChannelType,
) : ChannelCategory {

    var guildId : Snowflake = Snowflake(0)

    override var guild : Guild
        get() = runBlocking(client.coroutineContext) { client.user.fetchGuild(guildId) }
        set(value) {
            guildId = value.id
        }

    override suspend fun delete() {
        client.user.deleteChannel(this)
    }
}