package com.portto.fcl.utils

import com.portto.fcl.config.Network

object FclError {

    /**
     * General exception thrown from FCL.
     */
    class GeneralException(message: String) : Exception(message)

    /**
     * Exception thrown when the account is not found
     */
    class AccountNotFoundException : Exception("Account is not found.")

    /**
     * Exception thrown when the operation requires authentication
     */
    class UnauthenticatedException : Exception("The operation requires authentication.")

    /**
     * Exception thrown when the specific key is not found
     */
    class KeyNotFoundException : Exception("Cosigner key is not found.")

    /**
     * Exception thrown when the service is not available for the current user
     */
    class ServiceNotFoundException : Exception("Service is not available.")

    /**
     * Exception thrown when the signatures are unavailable
     */
    class SignaturesNotFoundException : Exception("Unable to fetch signatures.")

    /**
     * Exception thrown when the network is not specified
     */
    class UnspecifiedNetworkException : Exception("A Flow network must be specified.")

    /**
     * Exception thrown when authenticate is being called while wallet provider is not specified
     */
    class UnspecifiedWalletProviderException :
        Exception("A wallet provider is required to authenticate.")

    /**
     * Exception thrown when the provided [Network] is not supported
     */
    class UnsupportedNetworkException(network: Network) :
        Exception("${network.value} is not supported by FCL.")

    /**
     * Exception thrown when the user has rejected the operation
     */
    class UserRejectedException : Exception("User rejected.")
}