package com.portto.fcl.model.signable

import com.portto.fcl.model.PollingResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject

@Serializable
data class SignableUser(
    @SerialName("addr")
    val address: String? = null,
    @SerialName("keyId")
    val keyId: Int? = null,
    @SerialName("kind")
    val kind: String? = null,
    @SerialName("role")
    val role: Roles,
    @SerialName("tempId")
    var tempId: String? = null,
    @SerialName("sequenceNum")
    var sequenceNum: Int? = null,
    @SerialName("signature")
    var signature: String? = null,

    @Transient
    var signingFunction: (suspend (data: JsonObject) -> PollingResponse)? = null
)