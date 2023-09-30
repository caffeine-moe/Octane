package org.caffeine.octane.typedefs

enum class ConnectionType {
    CONNECT,
    DISCONNECT,
    RECONNECT,
    RECONNECT_AND_RESUME,
    KILL
}