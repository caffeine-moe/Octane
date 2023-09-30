package org.caffeine.octane.client.connection.handlers

import SerialGuild
import kotlinx.serialization.json.JsonElement
import org.caffeine.octane.client.ClientEvent
import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.client.connection.payloads.gateway.SerialPrivateChannel
import org.caffeine.octane.client.connection.payloads.gateway.ready.Ready
import org.caffeine.octane.client.connection.payloads.gateway.ready.ReadyDRelationship
import org.caffeine.octane.client.user.ClientUserImpl
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.asSnowflake
import org.caffeine.octane.entities.channels.DMChannelImpl
import org.caffeine.octane.entities.guild.Guild
import org.caffeine.octane.entities.users.User
import org.caffeine.octane.typedefs.ClientType
import org.caffeine.octane.typedefs.LogLevel
import org.caffeine.octane.typedefs.LoggerLevel
import org.caffeine.octane.utils.ConsoleColour
import org.caffeine.octane.utils.log
import org.caffeine.octane.utils.tryDecodeFromElement

suspend fun ready(client : ClientImpl, payload : JsonElement) {
    val d = tryDecodeFromElement<Ready>(payload) ?: return

    client.user = client.utils.createClientUser(d)
    client.userImpl.channels.putAll(extractPrivateChannels(d.private_channels, client))
    client.userImpl.guilds.putAll(extractGuilds(d.guilds, client))

    if (client.type == ClientType.USER) {
        (client.user as? ClientUserImpl)?.relationships?.putAll(
            extractRelationships(
                d.relationships,
                client
            )
        )
    }

    client.socket.sessionId = d.session_id
    client.socket.resumeGatewayUrl = d.resume_gateway_url
    client.socket.ready = true

    val time = System.currentTimeMillis() - client.socket.startTime

    log("${ConsoleColour.GREEN.value}Client logged in! ${time}ms", level = LogLevel(LoggerLevel.LOW, client))
    client.eventBus.produceEvent(ClientEvent.Ready(client.user, time))
}

fun extractGuilds(guilds : List<SerialGuild>, client : ClientImpl) : Map<Snowflake, Guild> =
    guilds.associateBy({ it.id.asSnowflake() }, { client.utils.createGuild(it) })

private fun extractRelationships(
    relationships : List<ReadyDRelationship>,
    client : ClientImpl,
) : Map<Snowflake, User> =
    relationships.associateBy({ it.user.id.asSnowflake() }, { client.utils.createUser(it.user, it.type) })

private fun extractPrivateChannels(
    channels : List<SerialPrivateChannel>,
    client : ClientImpl,
) : Map<Snowflake, DMChannelImpl> =
    channels.associateBy({ it.id.asSnowflake() }, { client.utils.createDMChannel(it) })