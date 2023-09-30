package org.caffeine.octane.client.connection.payloads.gateway

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentSlotResponse(
    val attachments : List<AttachmentSlot>,
)