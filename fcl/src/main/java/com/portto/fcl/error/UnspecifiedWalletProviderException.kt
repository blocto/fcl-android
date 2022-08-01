package com.portto.fcl.error

/**
 * Exception thrown when authenticate is being called while wallet provider is not specified
 */
class UnspecifiedWalletProviderException :
    Exception("A wallet provider is required to authenticate")