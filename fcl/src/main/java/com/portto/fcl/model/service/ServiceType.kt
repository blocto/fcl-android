package com.portto.fcl.model.service

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ServiceType {
    @SerialName("authn")
    AUTHN,

    @SerialName("authz")
    AUTHZ,

    @SerialName("pre-authz")
    PRE_AUTHZ,

    @SerialName("user-signature")
    USER_SIGNATURE,

    @SerialName("back-channel-rpc")
    BACK_CHANNEL,

    @SerialName("open-id")
    OPEN_ID,

    @SerialName("account-proof")
    ACCOUNT_PROOF,

    @SerialName("authn-refresh")
    AUTHN_REFRESH,

    @SerialName("local-view")
    LOCAL_VIEW,
}