package org.caffeine.octane.entities.users

import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.channels.DMChannel
import org.caffeine.octane.entities.channels.TextCapableChannel
import org.caffeine.octane.entities.message.Message
import org.caffeine.octane.entities.message.MessageSearchFilters
import org.caffeine.octane.typedefs.RelationshipType
import org.caffeine.octane.utils.CDN_URL

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