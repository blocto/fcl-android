package com.portto.fcl.model.signable

import kotlinx.serialization.Serializable

@Serializable
data class Signature(
    val address: String,
    val keyId: Int?,
    val sig: String?,
)
