package org.caffeine.octane.entities.nitro

import org.caffeine.octane.typedefs.RedeemedCodeErrorType
import org.caffeine.octane.typedefs.RedeemedCodeStatusType

data class RedeemedCode(
    var code : String = "",
    var latency : Long = -1,
    var status : RedeemedCodeStatusType = RedeemedCodeStatusType.INVALID,
    var error : RedeemedCodeErrorType = RedeemedCodeErrorType.NONE,
)