package client.connection.handlers

import kotlinx.serialization.json.JsonElement
import client.ClientEvent
import client.ClientImpl
import client.connection.payloads.gateway.MessageDelete
import entities.asSnowflake
import utils.tryDecodeFromElement

suspend fun messageDelete(payload : JsonElement, client : ClientImpl) {
    val data = tryDecodeFromElement<MessageDelete>(payload) ?: return
    val event = ClientEvent.MessageDelete(
        client.user.messageCache[data.id.asSnowflake()] ?: return
    )
    client.eventBus.produceEvent(event)
}