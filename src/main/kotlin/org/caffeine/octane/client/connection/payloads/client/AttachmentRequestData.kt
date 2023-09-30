package org.caffeine.octane.client.connection.payloads.client

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentRequestData(
    val files : List<AttachmentSlotRequest>,
)