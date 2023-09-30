package client.connection.payloads.client.bot.identify

import kotlinx.serialization.Serializable
import client.connection.OPCODE
import client.connection.payloads.client.BasePayload

@Serializable
class Identify(
    override val op : Int = OPCODE.IDENTIFY.value,
    val d : IdentifyD,
) : BasePayload()