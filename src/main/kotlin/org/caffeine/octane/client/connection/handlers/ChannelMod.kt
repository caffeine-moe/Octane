package org.caffeine.octane.client.connection.handlers

import kotlinx.serialization.json.JsonElement
import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.client.connection.payloads.gateway.SerialBaseChannel
import org.caffeine.octane.client.connection.payloads.gateway.SerialGuildChannel
import org.caffeine.octane.client.connection.payloads.gateway.SerialGuildVoiceChannel
import org.caffeine.octane.client.connection.payloads.gateway.SerialPrivateChannel
import org.caffeine.octane.entities.asSnowflake
import org.caffeine.octane.typedefs.ChannelType
import org.caffeine.octane.utils.tryDecodeFromElement

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