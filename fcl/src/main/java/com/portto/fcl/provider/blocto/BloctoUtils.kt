package com.portto.fcl.provider.blocto

import com.portto.sdk.wallet.BloctoSDKError

/**
 * Parse message from [BloctoSDKError]
 *
 * @return readable error message
 */
fun BloctoSDKError.parseErrorMessage() = message.split("_").joinToString(" ")