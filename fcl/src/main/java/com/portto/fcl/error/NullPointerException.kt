package com.portto.fcl.error

/**
 * Exception thrown if the required argument is null
 *
 * @param field the name which shall not be null
 * @param reason Optional; the additional explanation of the error
 */
class NullPointerException(field: String, reason: String? = null) :
    Exception("$field can not be null.${if (!reason.isNullOrEmpty()) " $reason" else ""}")