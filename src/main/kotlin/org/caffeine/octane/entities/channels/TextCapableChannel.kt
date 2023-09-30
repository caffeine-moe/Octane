package org.caffeine.octane.entities.channels

import kotlinx.coroutines.CompletableDeferred
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.message.Message
import org.caffeine.octane.entities.message.MessageFilters
import org.caffeine.octane.utils.MessageBuilder
import org.caffeine.octane.utils.MessageSendData

interface TextCapableChannel : BaseChannel {
    val lastMessageId : Snowflake
    suspend fun sendMessage(data : MessageSendData) : CompletableDeferred<Message> {
        return client.user.sendMessage(this, data)
    }

    suspend fun sendMessage(text : String) : CompletableDeferred<Message> {
        return client.user.sendMessage(this, MessageBuilder().append(text))
    }

    suspend fun fetchHistory(messageFilters : MessageFilters) : List<Message> {
        return client.user.fetchMessagesFromChannel(this, messageFilters)
    }

    suspend fun fetchMessageById(id : String) : Message {
        return client.user.fetchMessageById(id, this)
    }

    override suspend fun delete() {
        client.user.deleteChannel(this)
    }
}