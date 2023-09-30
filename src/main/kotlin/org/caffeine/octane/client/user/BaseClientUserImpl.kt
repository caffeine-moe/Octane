package org.caffeine.octane.client.user

import kotlinx.coroutines.CompletableDeferred
import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.channels.BaseChannel
import org.caffeine.octane.entities.channels.DMChannel
import org.caffeine.octane.entities.channels.GuildChannel
import org.caffeine.octane.entities.channels.TextCapableChannel
import org.caffeine.octane.entities.guild.Emoji
import org.caffeine.octane.entities.guild.Guild
import org.caffeine.octane.entities.guild.GuildMember
import org.caffeine.octane.entities.guild.Role
import org.caffeine.octane.entities.message.Message
import org.caffeine.octane.typedefs.RelationshipType
import org.caffeine.octane.utils.MessageSendData

open class BaseClientUserImpl(
    override val bio : String?,
    override val token : String,
    override val client : Client,
    override val username : String,
    override val id : Snowflake,
    override val avatar : String?,
    override val bot : Boolean,
    override val relation : RelationshipType = RelationshipType.NONE,
) : BaseClientUser {

    override val asMention : String = "<@${id}>"

    override var channels = HashMap<Snowflake, BaseChannel>()
    override var guilds = HashMap<Snowflake, Guild>()
    override var guildMembers = HashMap<Snowflake, GuildMember>()
    override val guildRoles = HashMap<Snowflake, Role>()
    override var emojis = HashMap<Snowflake, Emoji>()
    override val messageCache : HashMap<Snowflake, Message> = HashMap()
    override suspend fun sendMessage(
        channel : TextCapableChannel,
        message : MessageSendData,
    ) : CompletableDeferred<Message> =
        when (client.user) {
            is ClientUser -> client.user.sendMessage(channel, message)
            is BotClientUser -> client.user.sendMessage(channel, message)
            else -> (client.user as BotClientUser).sendMessage(channel, message)
        }


    override val dmChannels : Map<Snowflake, DMChannel>
        get() = channels.values.filterIsInstance<DMChannel>().associateBy { it.id }

    override val textChannels : Map<Snowflake, TextCapableChannel>
        get() = channels.values.filterIsInstance<TextCapableChannel>().associateBy { it.id }

    override val guildChannels : Map<Snowflake, GuildChannel>
        get() = channels.values.filterIsInstance<GuildChannel>().associateBy { it.id }
}