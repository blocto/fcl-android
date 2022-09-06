package com.portto.fcl.model.signable

import kotlinx.serialization.Serializable

/**
 * Arg
 *  {
 *    "type": "Int",
 *    "value": "1"
 *  }
 */
@Serializable
data class Arg(
    val type: String,
    val value: String
)
