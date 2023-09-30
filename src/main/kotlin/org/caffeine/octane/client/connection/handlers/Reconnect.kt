package org.caffeine.octane.client.connection.handlers

import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.typedefs.ClientType
import org.caffeine.octane.typedefs.ConnectionType
import org.caffeine.octane.typedefs.LogLevel
import org.caffeine.octane.typedefs.LoggerLevel
import org.caffeine.octane.utils.log

suspend fun reconnect(client : ClientImpl) {
    log("Gateway sent opcode 7 RECONNECT, reconnecting...", level = LogLevel(LoggerLevel.LOW, client))
    if (client.type != ClientType.BOT) {
        client.socket.execute(ConnectionType.CONNECT)
        return
    }
    client.socket.execute(ConnectionType.RECONNECT_AND_RESUME)
}