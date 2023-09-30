package org.caffeine.octane.client.connection.handlers

import kotlinx.serialization.json.JsonElement
import org.caffeine.octane.client.ClientEvent
import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.client.connection.payloads.gateway.guild.delete.GuildDeleteD
import org.caffeine.octane.entities.asSnowflake
import org.caffeine.octane.utils.tryDecodeFromElement

suspend fun guildDelete(payload : JsonElement, client : ClientImpl) {
    val parsed = tryDecodeFromElement<GuildDeleteD>(payload) ?: return
    val guild = client.user.guilds[parsed.id.asSnowflake()] ?: return
    client.userImpl.guilds.remove(parsed.id.asSnowflake())
    client.eventBus.produceEvent(ClientEvent.GuildDelete(guild))
}