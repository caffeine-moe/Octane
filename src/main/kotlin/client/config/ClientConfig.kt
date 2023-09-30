package client.config

import typedefs.APIVersion
import typedefs.ClientType
import typedefs.LoggerLevel
import typedefs.StatusType

data class ClientConfig(
    var token : String = "",
    var clientType : ClientType = ClientType.BOT,
    var statusType : StatusType = StatusType.ONLINE,
    var loggerLevel : LoggerLevel = LoggerLevel.NONE,
    var gatewayVersion : APIVersion = APIVersion.V9,
)