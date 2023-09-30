package org.caffeine.octane.entities.channels

import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.users.User

interface DMChannel : TextCapableChannel {
    val recipients : Map<Snowflake, User>
}