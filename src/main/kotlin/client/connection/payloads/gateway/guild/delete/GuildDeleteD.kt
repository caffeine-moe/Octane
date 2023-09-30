package client.connection.payloads.gateway.guild.delete

@kotlinx.serialization.Serializable
data class GuildDeleteD(
    val id : String = "",
)