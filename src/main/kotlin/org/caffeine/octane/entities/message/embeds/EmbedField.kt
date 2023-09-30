package org.caffeine.octane.entities.message.embeds

data class EmbedField(
    val name : String,
    val value : String,
    val inline : Boolean,
)