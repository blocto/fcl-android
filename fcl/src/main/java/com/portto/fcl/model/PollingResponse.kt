package com.portto.fcl.model

import com.portto.fcl.model.service.Service
import com.portto.fcl.network.ResponseStatus
import com.portto.fcl.utils.toDataClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement


@Serializable
data class PollingResponse(
    @SerialName("status")
    val status: ResponseStatus,
    @SerialName("reason")
    val reason: String?,
    @SerialName("data")
    val data: JsonElement?,
    @SerialName("updates")
    val updates: Service?,
    @SerialName("local")
    val local: JsonElement?,
    @SerialName("compositeSignature")
    val compositeSignature: AuthData?,
    @SerialName("authorizationUpdates")
    var authorizationUpdates: Service?,
) {
    fun local(): Service? {
        if (local == null) return null
        return try {
            if (local is JsonArray) return local.toDataClass<List<Service>>().first()
            else return local.toDataClass<Service>()
        } catch (e: Exception) {
            null
        }
    }
}
