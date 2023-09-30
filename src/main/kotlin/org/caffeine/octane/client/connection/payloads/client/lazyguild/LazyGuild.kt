package org.caffeine.octane.client.connection.payloads.client.lazyguild

import kotlinx.serialization.Serializable
import org.caffeine.octane.client.connection.OPCODE
import org.caffeine.octane.client.connection.payloads.client.BasePayload

@Serializable
data class LazyGuild(
    override val op : Int = OPCODE.LAZY_GUILD.value,
    val d : LazyGuildD = LazyGuildD(),
) : BasePayload()