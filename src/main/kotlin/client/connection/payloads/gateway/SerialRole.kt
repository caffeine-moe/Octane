package client.connection.payloads.gateway

import client.ClientImpl
import entities.asSnowflake
import entities.guild.Guild
import entities.guild.Role
import entities.guild.RoleImpl

@kotlinx.serialization.Serializable
data class SerialRole(
    val id : String,
    val name : String,
    val color : Int,
    val hoist : Boolean,
    val icon : String?,
    val unicode_emoji : String?,
    val position : Int,
    val permissions : Long,
    val managed : Boolean,
    val mentionable : Boolean,
) {
    fun createRole(guild : Guild,  client: ClientImpl) : Role {
        return RoleImpl(
            client,
            this.color,
            this.hoist,
            this.icon,
            this.id.asSnowflake(),
            this.managed,
            this.mentionable,
            this.name,
            client.utils.extractPermissions(this.permissions),
            this.position,
            this.unicode_emoji
        ).also { it.guild = guild }
    }
}