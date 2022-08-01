package com.portto.fcl.error

/**
 * Exception thrown if the required argument is null
 */
class NullPointerException(name: String) : Exception("$name can not be null")