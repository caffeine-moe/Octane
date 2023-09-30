package client.connection

import kotlinx.serialization.json.JsonElement
import client.ClientImpl
import client.connection.handlers.ready
import typedefs.ThemeType

enum class GatewayEvent(val value : String) {
    READY("READY"),
    MESSAGE_CREATE("MESSAGE_CREATE"),
    MESSAGE_UPDATE("MESSAGE_UPDATE"),
    MESSAGE_DELETE("MESSAGE_DELETE"),
    CHANNEL_CREATE("CHANNEL_CREATE"),
    CHANNEL_UPDATE("CHANNEL_UPDATE"),
    GUILD_DELETE("GUILD_DELETE"),
    GUILD_CREATE("GUILD_CREATE"),
    GUILD_UPDATE("GUILD_UPDATE"),
    GUILD_MEMBER_LIST_UPDATE("GUILD_MEMBER_LIST_UPDATE");
}