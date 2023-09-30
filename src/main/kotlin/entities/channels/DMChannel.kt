package entities.channels

import entities.Snowflake
import entities.users.User

interface DMChannel : TextCapableChannel {
    val recipients : Map<Snowflake, User>
}