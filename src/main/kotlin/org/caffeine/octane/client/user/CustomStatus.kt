package org.caffeine.octane.client.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.caffeine.octane.entities.Snowflake

@Serializable
data class CustomStatus(
    @SerialName("emoji_name")
    val emojiName : String? = null,
    @SerialName("emoji_id")
    val emojiId : Snowflake? = null,
    val expiresAt : Snowflake? = null,
    val text : String = "",
)