package entities.nitro

import typedefs.RedeemedCodeErrorType
import typedefs.RedeemedCodeStatusType

data class RedeemedCode(
    var code : String = "",
    var latency : Long = -1,
    var status : RedeemedCodeStatusType = RedeemedCodeStatusType.INVALID,
    var error : RedeemedCodeErrorType = RedeemedCodeErrorType.NONE,
)