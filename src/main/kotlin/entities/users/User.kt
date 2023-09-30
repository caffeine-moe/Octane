package entities.users

import client.Client
import entities.Snowflake
import entities.channels.DMChannel
import entities.channels.TextCapableChannel
import entities.message.Message
import entities.message.MessageSearchFilters
import typedefs.RelationshipType
import utils.CDN_URL

interface User {
    val client : Client
    val username : String
    val id : Snowflake
    val avatar : String?
    val bot : Boolean
    val relation : RelationshipType
    val asMention : String
    suspend fun fetchLastMessageInChannel(channel : TextCapableChannel, filters : MessageSearchFilters) : Message?
    fun avatarUrl() : String {
        val url = if (!avatar.isNullOrBlank()) {
            val ext = if (avatar!!.startsWith("a_"))
                ".gif"
            else
                ".png"
            "avatars/$id/$avatar$ext"
        } else
            "embed/avatars/${(id.value shr 22) % 6u}.png"

        return "$CDN_URL$url?size=512"
    }

    suspend fun openDM() : DMChannel? = client.user.openDMWith(id)
}