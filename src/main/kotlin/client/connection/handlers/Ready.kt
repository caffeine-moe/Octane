package client.connection.handlers

import SerialGuild
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonElement
import client.ClientEvent
import client.ClientImpl
import client.connection.payloads.gateway.SerialPrivateChannel
import client.connection.payloads.gateway.ready.Ready
import client.connection.payloads.gateway.ready.ReadyDRelationship
import client.user.ClientUserImpl
import entities.Snowflake
import entities.asSnowflake
import entities.channels.DMChannelImpl
import entities.guild.Guild
import entities.users.User
import typedefs.ClientType
import typedefs.LogLevel
import typedefs.LoggerLevel
import utils.ConsoleColour
import utils.json
import utils.log
import utils.tryDecodeFromElement

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