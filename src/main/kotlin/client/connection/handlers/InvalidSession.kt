package client.connection.handlers

import client.ClientImpl
import typedefs.ConnectionType
import typedefs.LogLevel
import typedefs.LoggerLevel
import utils.log

suspend fun invalidSession(client : ClientImpl) {
    log("Client received OPCODE 9 INVALID SESSION, reconnecting...", level = LogLevel(LoggerLevel.LOW, client))
    client.socket.execute(ConnectionType.RECONNECT)
}