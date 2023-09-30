package client.user

import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import client.Client
import client.ClientImpl
import client.connection.payloads.gateway.SerialMessage
import entities.Snowflake
import entities.channels.DMChannel
import entities.channels.GuildChannel
import entities.channels.TextCapableChannel
import entities.message.Message
import typedefs.RelationshipType
import utils.BASE_URL
import utils.MessageSendData
import utils.json

data class BotClientUserImpl(
    override val bio : String?,
    override val token : String,
    override val client : Client,
    override val username : String,
    override val id : Snowflake,
    override val avatar : String?,
    override val bot : Boolean,
    override val relation : RelationshipType = RelationshipType.NONE,
) : BotClientUser, BaseClientUserImpl(bio, token, client, username, id, avatar, bot) {

    override var asMention : String = "<@${id}>"

    private fun sendMessageWithAttachments(
        message : MessageSendData,
    ) : List<PartData> {
        client as ClientImpl
        var files = -1
        val data = json.encodeToString(message)
        val body = formData {

            append("payload_json", data, Headers.build {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
                append(HttpHeaders.ContentDisposition, "form-data; name=\"payload_json\"")
            })

            message.byteAttachments.map {
                files++
                val type = ContentType.fromFileExtension(it.key.replaceBefore(".", "")).toString()
                append("files[$files]", it.value, Headers.build {
                    append(HttpHeaders.ContentType, type)
                    append(HttpHeaders.ContentDisposition, "form-data; name=\"files[$files]\"; filename=\"${it.key}\"")
                })
            }
        }

        return body
    }

    override suspend fun sendMessage(
        channel : TextCapableChannel,
        message : MessageSendData,
    ) : CompletableDeferred<Message> {
        client as ClientImpl
        val response = if (message.byteAttachments.isEmpty()) {
            val data = json.encodeToString(message)
            client.httpClient.post("$BASE_URL/channels/${channel.id}/messages", data).await()
        } else {
            val data = sendMessageWithAttachments(message)
            client.httpClient.client.submitFormWithBinaryData("$BASE_URL/channels/${channel.id}/messages", data)
                .bodyAsText()
        }
        val serial = json.decodeFromString<SerialMessage>(response)
        return CompletableDeferred(client.utils.createMessage(serial))
    }

    override val dmChannels : Map<Snowflake, DMChannel>
        get() = channels.values.filterIsInstance<DMChannel>().associateBy { it.id }

    override val textChannels : Map<Snowflake, TextCapableChannel>
        get() = channels.values.filterIsInstance<TextCapableChannel>().associateBy { it.id }

    override val guildChannels : Map<Snowflake, GuildChannel>
        get() = channels.values.filterIsInstance<GuildChannel>().associateBy { it.id }

}