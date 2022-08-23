package com.portto.fcl.model.service

import com.portto.fcl.model.CompositeSignature
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ServiceData(
    @SerialName("f_type")
    val fclType: String,
    @SerialName("f_vsn")
    val fclVersion: String,
    @SerialName("email")
    val email: Email?,
    @SerialName("signatures")
    val signatures: List<CompositeSignature>?,
    @SerialName("address")
    val address: String?,
    @SerialName("nonce")
    val nonce: String?
) {
    @Serializable
    data class Email(
        @SerialName("email")
        val email: String,
        @SerialName("email_verified")
        val isVerified: Boolean
    )
}