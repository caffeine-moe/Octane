package client.connection.payloads.client.resume

import kotlinx.serialization.Serializable
import client.connection.OPCODE
import client.connection.payloads.client.BasePayload

@Serializable
data class Resume(
    override val op : Int = OPCODE.RESUME.value,
    val d : ResumeD = ResumeD(),
) : BasePayload()