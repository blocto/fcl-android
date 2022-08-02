package com.portto.fcl.sample.util

import com.portto.fcl.model.CompositeSignature


/**
 * Map Flow [CompositeSignature] to string for display
 */
fun List<CompositeSignature>.mapToString() = joinToString("\n\n") {
    "Address: ${it.address}\nKey ID: ${it.keyId}\nSignature: ${it.signature}"
}