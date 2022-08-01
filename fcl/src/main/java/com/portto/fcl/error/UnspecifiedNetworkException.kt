package com.portto.fcl.error

/**
 * Exception thrown when the network is not specified
 */
class UnspecifiedNetworkException : Exception("A Flow network must be specified")