package org.caffeine.octane.client.connection.handlers

import SerialGuild
import kotlinx.serialization.json.JsonElement
import org.caffeine.octane.client.ClientEvent
import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.utils.tryDecodeFromElement

suspend fun guildCreate(payload : JsonElement, client : ClientImpl) {
    val parsed = tryDecodeFromElement<SerialGuild>(payload) ?: return
    val guild = client.utils.createGuild(parsed)
    client.userImpl.guilds[guild.id] = guild
    client.eventBus.produceEvent(ClientEvent.GuildCreate(guild))
}