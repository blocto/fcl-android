package com.portto.fcl.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompositeSignature(
    @SerialName("addr")
    val address: String,

    @SerialName("keyId")
    val keyId: String,

    @SerialName("signature")
    val signature: String
)
