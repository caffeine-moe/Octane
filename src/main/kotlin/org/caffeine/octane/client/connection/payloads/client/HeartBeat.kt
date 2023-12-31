package org.caffeine.octane.client.connection.payloads.client

import kotlinx.serialization.Serializable

@Serializable
data class HeartBeat(
    override val op : Int,
    val d : Int?,
) : BasePayload()