package client.user

import kotlinx.coroutines.CompletableDeferred
import entities.Snowflake
import entities.guild.Guild
import entities.nitro.RedeemedCode
import entities.users.User
import typedefs.HypeSquadHouseType
import typedefs.StatusType
import typedefs.ThemeType

interface ClientUser : User {

    val verified : Boolean
    val email : String?
    val settings : ClientUserSettings
    val premium : Boolean?

    val relationships : Map<Snowflake, User>
    val friends : Map<Snowflake, User>
    val blocked : Map<Snowflake, User>
    suspend fun removeRelationship(user : User)

    suspend fun setHouse(house : HypeSquadHouseType)

    suspend fun setCustomStatus(status : CustomStatus)
    suspend fun setTheme(theme : ThemeType)

    suspend fun setStatus(status : StatusType)

    suspend fun redeemCode(code : String) : CompletableDeferred<RedeemedCode>

    suspend fun block(user : User)

    fun muteGuild(guild : Guild, i : Int)

}