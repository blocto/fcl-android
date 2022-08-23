package com.portto.fcl.model

import com.portto.fcl.model.service.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    @SerialName("f_type")
    val fclType: String?,

    @SerialName("f_vsn")
    val fclVersion: String?,

    @SerialName("addr")
    val address: String,

    @SerialName("services")
    val services: List<Service>
)
