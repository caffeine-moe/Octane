package client.connection.handlers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import client.ClientEvent
import client.ClientImpl
import client.connection.payloads.gateway.SerialMessage
import utils.json
import utils.tryDecodeFromElement
import utils.tryDecodeFromString

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