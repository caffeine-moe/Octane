package org.caffeine.octane.utils

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import org.caffeine.octane.entities.message.PartialAttachment
import org.caffeine.octane.entities.message.embeds.*
import org.caffeine.octane.typedefs.EmbedType
import java.io.File

interface MessageData {
    val content : String
    val tts : Boolean
    val nonce : String
    //val embed : MessageEmbed
}

interface MessageSendData : MessageData {

    var attachments : List<PartialAttachment>

    @Transient
    var byteAttachments : HashMap<String, ByteArray>
}

@Serializable
data class MessageReference(
    @Transient
    val guild_id : String? = null,
    val channel_id : String,
    val message_id : String,
)

@Serializable
data class MessageReply(
    override val content : String,
    override val tts : Boolean,
    override val nonce : String,
    @SerialName("message_reference")
    val messageReference : MessageReference,
) : MessageData

@Serializable
class MessageBuilder : MessageSendData {

    override var tts = false

    override var content : String = ""
    override val nonce : String get() = calcNonce()

    //override val embed : MessageEmbed = EmbedBuilder()
    override var attachments : List<PartialAttachment> = emptyList()

    @Transient
    override var byteAttachments : HashMap<String, ByteArray> = hashMapOf()


    fun append(text : String) : MessageBuilder {
        content += text
        return this
    }

    fun appendLine(text : String) : MessageBuilder {
        content += "\n$text"
        return this
    }

    fun addAttachment(attachment : File) : MessageBuilder {
        byteAttachments[attachment.name] = attachment.readBytes()
        return this
    }

    suspend fun addAttachmentFromURL(attachment : String) : MessageBuilder {
        val url = URLBuilder(urlString = attachment).build()
        val body = normalHTTPClient.get(url).readBytes()
        byteAttachments[url.pathSegments.last().toString()] = body
        return this
    }

    fun addAttachment(attachment : ByteArray, name : String) : MessageBuilder {
        byteAttachments[name] = attachment
        return this
    }

}

class EmbedBuilder {
    val title : String = ""
    val type : EmbedType = EmbedType.RICH
    val description : String = ""
    private val url : String = ""
    private val timestamp : Instant = Clock.System.now()
    val colour : Int = 100

    private lateinit var footer : EmbedFooter
    private lateinit var image : EmbedImage
    private lateinit var thumbnail : EmbedThumbnail
    private lateinit var video : EmbedVideo
    private lateinit var provider : EmbedProvider
    private lateinit var author : EmbedAuthor
    private val fields : List<EmbedField> = listOf()
}