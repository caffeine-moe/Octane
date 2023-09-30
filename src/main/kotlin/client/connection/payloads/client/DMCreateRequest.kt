package client.connection.payloads.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import entities.Snowflake

@Serializable
data class DMCreateRequest(
    @SerialName("recipient_id")
    val userId : Snowflake,
)