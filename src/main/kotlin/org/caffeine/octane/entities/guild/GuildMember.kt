package org.caffeine.octane.entities.guild

import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.users.User
import org.caffeine.octane.typedefs.PermissionType

interface GuildMember {
    val id : Snowflake
    val user : User
    val nickname : String
    val roles : Map<Snowflake, Role>
    val permissions : List<PermissionType>
    val joinedAt : Snowflake
    val premiumSince : Snowflake?
    val deafened : Boolean
    val muted : Boolean
    val guild : Guild
    val client : Client
}