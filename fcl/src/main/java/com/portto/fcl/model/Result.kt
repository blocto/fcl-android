package com.portto.fcl.model

/**
 * FCL result wrapper
 */
sealed class Result<out T> {
    data class Success<out R>(val value: R) : Result<R>()
    data class Failure(val throwable: Throwable) : Result<Nothing>()
}
