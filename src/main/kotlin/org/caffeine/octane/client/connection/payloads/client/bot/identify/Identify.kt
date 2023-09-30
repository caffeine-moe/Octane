package org.caffeine.octane.client.connection.payloads.client.bot.identify

import kotlinx.serialization.Serializable
import org.caffeine.octane.client.connection.OPCODE
import org.caffeine.octane.client.connection.payloads.client.BasePayload

@Serializable
class Identify(
    override val op : Int = OPCODE.IDENTIFY.value,
    val d : IdentifyD,
) : BasePayload()