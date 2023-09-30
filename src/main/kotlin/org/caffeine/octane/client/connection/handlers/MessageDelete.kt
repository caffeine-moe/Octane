package org.caffeine.octane.client.connection.handlers

import kotlinx.serialization.json.JsonElement
import org.caffeine.octane.client.ClientEvent
import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.client.connection.payloads.gateway.MessageDelete
import org.caffeine.octane.entities.asSnowflake
import org.caffeine.octane.utils.tryDecodeFromElement

suspend fun messageDelete(payload : JsonElement, client : ClientImpl) {
    val data = tryDecodeFromElement<MessageDelete>(payload) ?: return
    val event = ClientEvent.MessageDelete(
        client.user.messageCache[data.id.asSnowflake()] ?: return
    )
    client.eventBus.produceEvent(event)
}