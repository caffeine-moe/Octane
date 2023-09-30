package org.caffeine.octane.entities.guild

import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake

interface Emoji {
    val client : Client
    val animated : Boolean
    val id : Snowflake
    val guild : Guild
    val name : String
    val url : String
}