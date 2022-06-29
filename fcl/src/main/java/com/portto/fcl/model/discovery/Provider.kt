package com.portto.fcl.model.discovery

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Provider(
    @SerialName("address")
    val address: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("color")
    val color: String,
    @SerialName("supportEmail")
    val supportEmail: String,
    @SerialName("authn_endpoint")
    val authnEndpoint: String? = null,
    @SerialName("website")
    val website: String?
)