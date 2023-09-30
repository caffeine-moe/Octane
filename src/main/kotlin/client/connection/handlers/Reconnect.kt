package client.connection.handlers

import client.ClientImpl
import typedefs.ClientType
import typedefs.ConnectionType
import typedefs.LogLevel
import typedefs.LoggerLevel
import utils.log

suspend fun reconnect(client : ClientImpl) {
    log("Gateway sent opcode 7 RECONNECT, reconnecting...", level = LogLevel(LoggerLevel.LOW, client))
    if (client.type != ClientType.BOT) {
        client.socket.execute(ConnectionType.CONNECT)
        return
    }
    client.socket.execute(ConnectionType.RECONNECT_AND_RESUME)
}