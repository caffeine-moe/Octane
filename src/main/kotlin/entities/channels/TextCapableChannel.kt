package entities.channels

import kotlinx.coroutines.CompletableDeferred
import entities.Snowflake
import entities.message.Message
import entities.message.MessageFilters
import utils.MessageBuilder
import utils.MessageSendData

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