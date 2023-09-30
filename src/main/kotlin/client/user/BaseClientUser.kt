package client.user

import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import client.ClientImpl
import client.connection.payloads.client.DMCreateRequest
import client.connection.payloads.gateway.SerialMessage
import client.connection.payloads.gateway.SerialPrivateChannel
import entities.Snowflake
import entities.channels.BaseChannel
import entities.channels.DMChannel
import entities.channels.GuildChannel
import entities.channels.TextCapableChannel
import entities.guild.Emoji
import entities.guild.Guild
import entities.guild.GuildMember
import entities.guild.Role
import entities.message.Message
import entities.message.MessageFilters
import entities.message.MessageSearchFilters
import entities.users.User
import utils.*

interface BaseClientUser : User {

    val bio : String?
    val token : String

    private val clientImpl : ClientImpl
        get() = client as ClientImpl

    val guilds : Map<Snowflake, Guild>
    val guildMembers : Map<Snowflake, GuildMember>
    val guildRoles : Map<Snowflake, Role>
    val emojis : Map<Snowflake, Emoji>
    val channels : Map<Snowflake, BaseChannel>
    val guildChannels : Map<Snowflake, GuildChannel>
    val textChannels : Map<Snowflake, TextCapableChannel>
    val dmChannels : Map<Snowflake, DMChannel>
    val messageCache : Map<Snowflake, Message>

    suspend fun sendMessage(channel : TextCapableChannel, message : MessageSendData) : CompletableDeferred<Message>
    suspend fun sendMessage(channel : TextCapableChannel, message : String) : CompletableDeferred<Message> =
        sendMessage(channel, MessageBuilder().append(message))

    suspend fun editMessage(message : Message, edit : MessageSendData) : CompletableDeferred<Message> {
        client as ClientImpl
        val data = jsonNoDefaults.encodeToString(edit)
        val response =
            clientImpl.httpClient.patch(
                "$BASE_URL/channels/${message.channel.id}/messages/${message.id}",
                data
            ).await()
        val serial = json.decodeFromString<SerialMessage>(response)
        return CompletableDeferred(clientImpl.utils.createMessage(serial))
    }

    suspend fun deleteMessage(message : Message) =
        clientImpl.httpClient.delete("$BASE_URL/channels/${message.channel.id}/messages/${message.id}").await()

    suspend fun fetchMessageById(id : String, channel : TextCapableChannel) : Message =
        clientImpl.utils.fetchMessageById(id, channel)

    suspend fun fetchMessagesFromChannel(
        channel : TextCapableChannel,
        filters : MessageFilters,
    ) : List<Message> = clientImpl.utils.fetchMessages(channel, filters)

    suspend fun fetchGuildMember(user : User, guild : Guild) : GuildMember =
        clientImpl.utils.fetchGuildMember(user.id, guild)

    suspend fun fetchChannelFromId(id : Snowflake) : BaseChannel? = this.channels[id]

    override suspend fun fetchLastMessageInChannel(
        channel : TextCapableChannel,
        filters : MessageSearchFilters,
    ) : Message? {
        return clientImpl.utils.fetchLastMessageInChannel(channel, this, filters)
    }

    suspend fun replyMessage(message : Message, data : MessageData) : CompletableDeferred<Message> {
        val reply = clientImpl.utils.createReply(message, data)
        val jsondata = json.encodeToString(reply)
        val response =
            clientImpl.httpClient.post("$BASE_URL/channels/${message.channel.id}/messages", jsondata).await()
        val serial = json.decodeFromString<SerialMessage>(response)
        return CompletableDeferred(clientImpl.utils.createMessage(serial))
    }

    suspend fun deleteChannel(channel : BaseChannel) {
        clientImpl.httpClient.delete("$BASE_URL/channels/${channel.id}")
    }

    suspend fun fetchGuild(guildId : Snowflake) : Guild = clientImpl.utils.fetchGuild(guildId)

    suspend fun openDMWith(id : Snowflake) : DMChannel {
        return dmChannels.values.firstOrNull { it.recipients.containsKey(id) && it.recipients.size == 1 }
            ?: kotlin.run {
                val response = clientImpl.httpClient.post(
                    "$BASE_URL/users/@me/channels",
                    json.encodeToString(
                        DMCreateRequest(id)
                    )
                ).await()
                val serial = json.decodeFromString<SerialPrivateChannel>(response)
                return clientImpl.utils.createDMChannel(serial)
            }
    }

    suspend fun fetchUser(userId : Snowflake) : User = clientImpl.utils.fetchUser(userId)
}