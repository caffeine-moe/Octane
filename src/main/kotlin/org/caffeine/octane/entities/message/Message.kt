package org.caffeine.octane.entities.message

import kotlinx.coroutines.CompletableDeferred
import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.channels.TextCapableChannel
import org.caffeine.octane.entities.guild.Guild
import org.caffeine.octane.entities.message.embeds.MessageEmbed
import org.caffeine.octane.entities.users.User
import org.caffeine.octane.typedefs.MessageType
import org.caffeine.octane.utils.MessageData
import org.caffeine.octane.utils.MessageSendData

interface Message : MessageData {
    val client : Client
    val id : Snowflake
    val channel : TextCapableChannel
    val guild : Guild?
    val author : User
    val timestamp : Long
    val editedAt : Long?
    val mentionedEveryone : Boolean
    val mentions : Map<String, User>
    val pinned : Boolean
    val type : MessageType
    var attachments : Map<Snowflake, MessageAttachment>
    val embeds : List<MessageEmbed>
    val mentionsSelf : Boolean

    suspend fun delete() : Boolean

    suspend fun edit(edit : MessageSendData) : CompletableDeferred<Message>

    suspend fun edit(text : String) : CompletableDeferred<Message>

    suspend fun reply(text : MessageSendData) : CompletableDeferred<Message>

    suspend fun reply(text : String) : CompletableDeferred<Message>
}