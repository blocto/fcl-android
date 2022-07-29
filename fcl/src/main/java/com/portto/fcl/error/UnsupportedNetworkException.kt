package com.portto.fcl.error

import com.portto.fcl.config.NetworkEnv

/**
 * Exception thrown when the provided [NetworkEnv] is not supported
 */
class UnsupportedNetworkException(networkEnv: NetworkEnv) :
    Exception("${networkEnv.value} is not supported by FCL")