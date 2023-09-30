package typedefs

import client.Client

data class LogLevel(val level : LoggerLevel, val client : Client)

enum class LoggerLevel {
    NONE,
    LOW,
    MEDIUM,
    ALL;
}