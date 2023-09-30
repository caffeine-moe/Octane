package entities.guild

import kotlinx.coroutines.runBlocking
import client.Client
import entities.Snowflake
import entities.users.User
import typedefs.PermissionType

data class GuildMemberImpl(
    override var id : Snowflake,
    override var user : User,
    override var nickname : String,
    override var joinedAt : Snowflake,
    override var premiumSince : Snowflake?,
    override var deafened : Boolean,
    override var muted : Boolean,
    override var client : Client,
) : GuildMember {

    var guildId = Snowflake(0)

    var roleIds = listOf<Snowflake>()

    override val roles : Map<Snowflake, Role>
        get() = client.user.guildRoles.filterValues { it.guild.id == guild.id && it.id in roleIds }

    override var guild : Guild
        get() = runBlocking(client.coroutineContext) { client.user.fetchGuild(guildId) }
        set(value) {
            guildId = value.id
        }

    override val permissions : List<PermissionType> get() = roles.values.flatMap { it.permissions }.distinct()
}