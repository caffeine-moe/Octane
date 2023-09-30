package org.caffeine.octane.entities.channels

import org.caffeine.octane.client.Client
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.users.User
import org.caffeine.octane.typedefs.ChannelType

data class DMChannelImpl(
    override val id : Snowflake,
    override val client : Client,
    override val lastMessageId : Snowflake,
    override val name : String,
    override val recipients : Map<Snowflake, User>,
    override val type : ChannelType,
) : DMChannel