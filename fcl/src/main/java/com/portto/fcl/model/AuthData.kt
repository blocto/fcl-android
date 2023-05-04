package com.portto.fcl.model

import com.portto.fcl.model.service.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    @SerialName("addr")
    val address: String?, // authn
    @SerialName("services")
    val services: List<Service>?, // authn
    @SerialName("f_type")
    val fclType: String?,
    @SerialName("f_vsn")
    val fclVersion: String?,
    @SerialName("signature")
    val signature: String?,

    @SerialName("proposer")
    val proposer: Service?, // pre-authz (blocto only)
    @SerialName("payer")
    val payer: List<Service>?, // pre-authz (blocto only)
    @SerialName("authorization")
    val authorization: List<Service>?, // pre-authz (blocto only)
    @SerialName("code")
    val sessionId: String?, // used for web sdk (blocto only)
)
