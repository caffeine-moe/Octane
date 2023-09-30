package entities.channels

import client.Client
import entities.Snowflake
import entities.users.User
import typedefs.ChannelType

data class DMChannelImpl(
    override val id : Snowflake,
    override val client : Client,
    override val lastMessageId : Snowflake,
    override val name : String,
    override val recipients : Map<Snowflake, User>,
    override val type : ChannelType,
) : DMChannel