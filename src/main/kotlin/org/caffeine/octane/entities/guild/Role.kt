package org.caffeine.octane.entities.guild

import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.typedefs.PermissionType

interface Role {
    val color : Int
    val hoisted : Boolean
    val icon : String?
    val id : Snowflake
    val managed : Boolean
    val mentionable : Boolean
    val name : String
    val permissions : List<PermissionType>
    val position : Int
    val unicodeEmoji : String?
    val guild : Guild
    val client : Client
}