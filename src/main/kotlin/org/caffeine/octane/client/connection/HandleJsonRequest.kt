package org.caffeine.octane.client.connection

import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.client.connection.handlers.*
import org.caffeine.octane.client.connection.payloads.gateway.BasePayload
import org.caffeine.octane.utils.tryDecodeFromString

suspend fun handleJsonRequest(payload : String, client : ClientImpl) {
    val event = tryDecodeFromString<BasePayload>(payload) ?: return
    client.socket.gatewaySequence = event.s ?: client.socket.gatewaySequence
    when (event.op) {
        OPCODE.DISPATCH.value -> {
            val data = event.d ?: return
            if (!isReady(client, event)) return
            when (event.t) {

                GatewayEvent.MESSAGE_CREATE.value -> messageCreate(data, client)

                GatewayEvent.MESSAGE_UPDATE.value -> messageUpdate(data, client)

                GatewayEvent.MESSAGE_DELETE.value -> messageDelete(data, client)

                GatewayEvent.CHANNEL_CREATE.value -> channelMod(data, client)

                GatewayEvent.CHANNEL_UPDATE.value -> channelMod(data, client)

                GatewayEvent.GUILD_DELETE.value -> guildDelete(data, client)

                GatewayEvent.GUILD_CREATE.value -> guildCreate(data, client)

                GatewayEvent.GUILD_UPDATE.value -> guildUpdate(data, client)

                //GatewayEvent.GUILD_MEMBER_LIST_UPDATE.value -> guildMemberListUpdate(data, client)

            }
        }

        OPCODE.HEARTBEAT.value -> client.socket.sendHeartBeat()

        OPCODE.RECONNECT.value -> reconnect(client)

        OPCODE.INVALID_SESSION.value -> invalidSession(client)

        OPCODE.HEARTBEAT_ACK.value -> return

        else -> {
            println(payload)
        }
    }
}

private suspend fun isReady(client : ClientImpl, payload : BasePayload) : Boolean {
    if (payload.op == OPCODE.DISPATCH.value && payload.t == GatewayEvent.READY.value && payload.d != null)
        ready(client, payload.d)
    return client.socket.ready
}