package client.connection.payloads.client.user.identify

import kotlinx.serialization.Serializable
import utils.DiscordUtils

@Serializable
data class IdentifyD(
    val token : String,
    val capabilities : Int = 2048,
    val properties : DiscordUtils.SuperProperties,
    val presence : IdentifyDPresence = IdentifyDPresence(),
    val compress : Boolean = false,
    val client_state : IdentifyDClientState = IdentifyDClientState(),
)