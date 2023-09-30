package client.connection.handlers

import SerialGuild
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonElement
import client.ClientEvent
import client.ClientImpl
import utils.json
import utils.tryDecodeFromElement

suspend fun guildCreate(payload : JsonElement, client : ClientImpl) {
    val parsed = tryDecodeFromElement<SerialGuild>(payload) ?: return
    val guild = client.utils.createGuild(parsed)
    client.userImpl.guilds[guild.id] = guild
    client.eventBus.produceEvent(ClientEvent.GuildCreate(guild))
}