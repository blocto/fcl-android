package com.portto.fcl.model.discovery

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Service(
    @SerialName("f_type")
    val fType: String? = null,
    @SerialName("f_vsn")
    val fVsn: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("method")
    val method: String? = null,
    @SerialName("uid")
    val uid: String? = null,
    @SerialName("endpoint")
    val endpoint: String? = null,
    @SerialName("optIn")
    val optIn: Boolean? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("provider")
    val provider: Provider? = null,
    @SerialName("params")
    val params: Map<String, String>? = null,
)