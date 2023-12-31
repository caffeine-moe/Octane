package org.caffeine.octane.client.connection

import io.ktor.client.plugins.websocket.*
import io.ktor.utils.io.core.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromJsonElement
import org.caffeine.octane.client.ClientImpl
import org.caffeine.octane.client.connection.payloads.client.HeartBeat
import org.caffeine.octane.client.connection.payloads.gateway.BasePayload
import org.caffeine.octane.client.connection.payloads.gateway.init.Init
import org.caffeine.octane.typedefs.ClientType
import org.caffeine.octane.typedefs.ConnectionType
import org.caffeine.octane.typedefs.LogLevel
import org.caffeine.octane.typedefs.LoggerLevel
import org.caffeine.octane.utils.*
import java.io.ByteArrayOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterOutputStream
import kotlin.text.Charsets.UTF_8
import kotlin.time.Duration.Companion.milliseconds


class Connection(private val client : ClientImpl) {

    var startTime : Long = 0
    var ready = false
    var gatewaySequence = 0
    var resumeGatewayUrl : String = ""
    var sessionId = ""

    private lateinit var inflater : Inflater

    private lateinit var webSocket : DefaultClientWebSocketSession

    private var heartBeat = Job() as Job

    suspend fun execute(type : ConnectionType) {
        when (type) {
            ConnectionType.CONNECT -> connect(client.utils.generateIdentify())
            ConnectionType.DISCONNECT -> disconnect()
            ConnectionType.RECONNECT -> reconnect()
            ConnectionType.RECONNECT_AND_RESUME -> reconnectResume()
            ConnectionType.KILL -> disconnect(true)
        }
    }

    suspend fun sendHeartBeat() = try {
        webSocket.ensureActive()
        val heartbeat = json.encodeToString(
            HeartBeat(
                OPCODE.HEARTBEAT.value,
                gatewaySequence
            )
        )
        webSocket.send(heartbeat)
    } catch (e : Exception) {
        reconnect()
    }

    private suspend fun startHeartBeat(interval : Long) {
        log("Heartbeat started.", level = LogLevel(LoggerLevel.LOW, client))
        while (true) {
            sendHeartBeat()
            delay(interval.milliseconds)
        }
    }

    private suspend fun readSocket() {
        webSocket.incoming.receiveAsFlow().buffer(Channel.UNLIMITED).collect {
            when (it) {
                is Frame.Binary, is Frame.Text -> handleJsonRequest(it.deflateData(), client)
                else -> { /*ignore*/
                }
            }
        }
    }

    private suspend fun connect(payload : DiscordUtils.PayloadDef) {
        fetchWebClientValues(client)
        client.utils.createSuperProperties()
        //if (client.type != ClientType.BOT) client.utils.tokenValidator(client.configuration.token)

        inflater = Inflater()

        client.httpClient.client.wss(
            host = if (payload.type != DiscordUtils.PayloadType.RESUME) GATEWAY
            else resumeGatewayUrl.removePrefix("wss://").ifBlank { GATEWAY },
            path = "/?encoding=json&v=${client.configuration.gatewayVersion.value}&compress=zlib-stream",
            port = 443
        ) {
            startTime = System.currentTimeMillis()

            webSocket = this

            log(
                "${ConsoleColour.GREEN.value}Connected to the Discord gateway!",
                level =
                LogLevel(LoggerLevel.LOW, client)
            )

            val event = incoming.receive().deflateData()
            val init = json.decodeFromString<BasePayload>(event)
            when (init.op) {
                OPCODE.HELLO.value -> {
                    val data = json.decodeFromJsonElement<Init>(init.d ?: return@wss)

                    log(
                        "Client received OPCODE 10 HELLO, sending ${payload.name} payload and starting heartbeat.",
                        level =
                        LogLevel(LoggerLevel.LOW, client)
                    )
                    heartBeat = launch { startHeartBeat(data.heartbeat_interval) }

                    if (payload.type == DiscordUtils.PayloadType.RESUME) delay((6000).milliseconds)

                    send(Frame.Text(payload.payload))

                    log("${payload.name} sent.", level = LogLevel(LoggerLevel.LOW, client))

                    readSocket()
                }

                else -> {
                    println(init)
                }
            }
            awaitCancellation()
        }
    }

    private fun Frame.deflateData() : String {
        val outputStream = ByteArrayOutputStream()
        InflaterOutputStream(outputStream, inflater).use {
            it.write(data)
        }

        return outputStream.use {
            String(outputStream.toByteArray(), 0, outputStream.size(), UTF_8)
        }
    }

    private suspend fun disconnect(kill : Boolean = false) {
        heartBeat.cancelAndJoin()
        webSocket.close()
        ready = false
        log(
            "Client logged out.", level =
            LogLevel(LoggerLevel.LOW, client)
        )
        if (kill) webSocket.cancel("Killed.")
    }

    private suspend fun reconnectResume() {
        disconnect()
        connect(client.utils.generateResume())
    }

    private suspend fun reconnect() {
        disconnect()
        execute(ConnectionType.CONNECT)
    }
}