package entities.message

import kotlinx.coroutines.CompletableDeferred
import client.Client
import entities.Snowflake
import entities.channels.TextCapableChannel
import entities.guild.Guild
import entities.message.embeds.MessageEmbed
import entities.users.User
import typedefs.MessageType
import utils.MessageData
import utils.MessageSendData

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