package client.connection.payloads.gateway

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement

@Serializable
data class BasePayload (
     val op : Int,
     val s : Int?,
     val t : String?,
     val d : JsonElement? = null
)