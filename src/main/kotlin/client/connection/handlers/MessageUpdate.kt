package client.connection.handlers

import kotlinx.serialization.json.JsonElement
import client.ClientEvent
import client.ClientImpl
import client.connection.payloads.gateway.MessageUpdate
import entities.asSnowflake
import entities.message.MessageImpl
import utils.tryDecodeFromElement

suspend fun messageUpdate(payload : JsonElement, client : ClientImpl) {
    val update = tryDecodeFromElement<MessageUpdate>(payload) ?: return
    val old = client.user.messageCache[update.id.asSnowflake()] ?: return
    old as MessageImpl
    val new = old.copy(
        content = update.content ?: old.content,
        editedAt = client.utils.timestampResolver(update.edited_timestamp)
    )
    client.userImpl.messageCache[old.id] = new
    val event = ClientEvent.MessageEdit(
        new
    )
    client.eventBus.produceEvent(event)
}