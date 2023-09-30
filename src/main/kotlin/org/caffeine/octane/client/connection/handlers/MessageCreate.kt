package org.caffeine.octane.client.connection.handlers

import kotlinx.serialization.json.JsonElement
import org.caffeine.octane.client.ClientEvent
import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.client.connection.payloads.gateway.SerialMessage
import org.caffeine.octane.utils.tryDecodeFromElement

suspend fun messageCreate(payload : JsonElement, client : ClientImpl) {
    val message = tryDecodeFromElement<SerialMessage>(payload) ?: return
    val result = client.utils.createMessage(message)
    client.userImpl.messageCache[result.id] = result
    val event = ClientEvent.MessageCreate(
        result,
        result.channel
    )
    client.eventBus.produceEvent(event)
}