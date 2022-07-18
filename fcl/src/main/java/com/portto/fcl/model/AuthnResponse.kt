package com.portto.fcl.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthnResponse(
    @SerialName("addr")
    val address: String
)
