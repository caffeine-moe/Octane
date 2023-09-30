package org.caffeine.octane.client.connection.payloads.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.caffeine.octane.entities.Snowflake

@Serializable
data class DMCreateRequest(
    @SerialName("recipient_id")
    val userId : Snowflake,
)