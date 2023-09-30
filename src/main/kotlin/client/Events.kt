package client

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import client.user.BaseClientUser
import entities.channels.TextCapableChannel
import entities.guild.Guild
import entities.message.Message


interface ClientEvent {
    class Ready(val user : BaseClientUser, val time : Long) : ClientEvent
    class MessageCreate(val message : Message, val channel : TextCapableChannel) : ClientEvent
    class GuildCreate(val guild : Guild) : ClientEvent
    class GuildUpdate(val old : Guild, val new : Guild) : ClientEvent

    class GuildDelete(val guild : Guild) : ClientEvent
    class MessageEdit(val message : Message) : ClientEvent
    class MessageDelete(val message : Message) : ClientEvent
}

class EventBus {
    private val _events = MutableSharedFlow<ClientEvent>() // private mutable shared flow
    val flow = _events.asSharedFlow() // publicly exposed as read-only shared flow

    suspend fun produceEvent(event : ClientEvent) {
        _events.emit(event) // suspends until all subscribers receive it
    }
}