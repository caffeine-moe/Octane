package client.connection.payloads.client.lazyguild

import kotlinx.serialization.Serializable
import client.connection.OPCODE
import client.connection.payloads.client.BasePayload

@Serializable
data class LazyGuild(
    override val op : Int = OPCODE.LAZY_GUILD.value,
    val d : LazyGuildD = LazyGuildD(),
) : BasePayload()