package client.connection.handlers

import kotlinx.serialization.json.JsonElement
import client.ClientImpl
import client.connection.payloads.gateway.SerialBaseChannel
import client.connection.payloads.gateway.SerialGuildChannel
import client.connection.payloads.gateway.SerialGuildVoiceChannel
import client.connection.payloads.gateway.SerialPrivateChannel
import entities.asSnowflake
import typedefs.ChannelType
import utils.tryDecodeFromElement

suspend fun channelMod(payload : JsonElement, client : ClientImpl) {
    val base = tryDecodeFromElement<SerialBaseChannel>(payload) ?: return
    val channel = when (ChannelType.enumById(base.type)) {
        ChannelType.TEXT, ChannelType.CATEGORY -> {
            val c = tryDecodeFromElement<SerialGuildChannel>(payload) ?: return
            client.utils.createGuildChannel(c, client.utils.fetchGuild(c.guild_id.asSnowflake()))
        }

        ChannelType.DM ->
            client.utils.createDMChannel(tryDecodeFromElement<SerialPrivateChannel>(payload) ?: return)

        ChannelType.VOICE -> {
            val c = tryDecodeFromElement<SerialGuildVoiceChannel>(payload) ?: return
            client.utils.createVC(c, client.utils.fetchGuild(c.guild_id.asSnowflake()))
        }

        else -> return
    }
    client.userImpl.channels[channel.id] = channel
}