package client.connection.handlers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonElement
import client.ClientEvent
import client.ClientImpl
import client.connection.payloads.gateway.guild.delete.GuildDeleteD
import entities.asSnowflake
import utils.json
import utils.tryDecodeFromElement

suspend fun guildDelete(payload : JsonElement, client : ClientImpl) {
    val parsed = tryDecodeFromElement<GuildDeleteD>(payload) ?: return
    val guild = client.user.guilds[parsed.id.asSnowflake()] ?: return
    client.userImpl.guilds.remove(parsed.id.asSnowflake())
    client.eventBus.produceEvent(ClientEvent.GuildDelete(guild))
}