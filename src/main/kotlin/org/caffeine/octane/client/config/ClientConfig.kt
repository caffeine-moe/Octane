package org.caffeine.octane.client.config

import org.caffeine.octane.typedefs.APIVersion
import org.caffeine.octane.typedefs.ClientType
import org.caffeine.octane.typedefs.LoggerLevel
import org.caffeine.octane.typedefs.StatusType

data class ClientConfig(
    var token : String = "",
    var clientType : ClientType = ClientType.BOT,
    var statusType : StatusType = StatusType.ONLINE,
    var loggerLevel : LoggerLevel = LoggerLevel.NONE,
    var gatewayVersion : APIVersion = APIVersion.V9,
)