package entities.guild

import client.Client
import entities.Snowflake
import entities.users.User
import typedefs.PermissionType

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