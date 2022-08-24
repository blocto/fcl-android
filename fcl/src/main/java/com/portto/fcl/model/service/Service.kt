package com.portto.fcl.model.service


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Service(
    @SerialName("f_type")
    val fclType: String?,
    @SerialName("f_vsn")
    val fclVersion: String?,
    @SerialName("type")
    val type: ServiceType? = null, // blocto only
    @SerialName("uid")
    val uid: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("identity")
    val identity: Identity?,
    @SerialName("scoped")
    val scoped: Scoped?,
    @SerialName("provider")
    val provider: Provider?,
    @SerialName("authn")
    val authn: String?,
    @SerialName("method")
    val method: String?,
    @SerialName("address")
    val address: String?,
    @SerialName("keyId")
    val keyId: Int?,
    @SerialName("endpoint")
    val endpoint: String?,
    @SerialName("params")
    val params: Map<String, String>?,
    @SerialName("data")
    val data: ServiceData?
) {
    @Serializable
    data class Identity(
        @SerialName("address")
        val address: String,
        @SerialName("keyId")
        val keyId: Int?,
    )

    @Serializable
    data class Scoped(
        @SerialName("email")
        val email: String
    )

    @Serializable
    data class Provider(
        @SerialName("address")
        val address: String,
        @SerialName("name")
        val name: String,
        @SerialName("icon")
        val icon: String?, // blocto only
        @SerialName("description")
        val description: String? // blocto only
    )
}