package org.caffeine.octane.client.connection.handlers

import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.typedefs.ConnectionType
import org.caffeine.octane.typedefs.LogLevel
import org.caffeine.octane.typedefs.LoggerLevel
import org.caffeine.octane.utils.log

suspend fun invalidSession(client : ClientImpl) {
    log("Client received OPCODE 9 INVALID SESSION, reconnecting...", level = LogLevel(LoggerLevel.LOW, client))
    client.socket.execute(ConnectionType.RECONNECT)
}