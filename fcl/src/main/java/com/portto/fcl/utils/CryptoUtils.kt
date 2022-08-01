package com.portto.fcl.utils


/**
 * Remove prefix from the address
 */
fun String.sansPrefix(): String {
    if (isEmpty()) throw Exception("No address provided")
    return replace("0x", "")
}

/**
 * Add hex prefix (remove prefix if it exists beforehand)
 */
fun String.withPrefix(): String {
    if (isEmpty()) throw Exception("No address provided")
    return "0x${sansPrefix()}"
}