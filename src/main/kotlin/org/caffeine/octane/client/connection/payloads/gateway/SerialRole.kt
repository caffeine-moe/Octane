package org.caffeine.octane.client.connection.payloads.gateway

import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.entities.asSnowflake
import org.caffeine.octane.entities.guild.Guild
import org.caffeine.octane.entities.guild.Role
import org.caffeine.octane.entities.guild.RoleImpl

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