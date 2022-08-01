package com.portto.fcl.error

import com.portto.fcl.config.Network

/**
 * Exception thrown when the provided [Network] is not supported
 */
class UnsupportedNetworkException(network: Network) :
    Exception("${network.value} is not supported by FCL")