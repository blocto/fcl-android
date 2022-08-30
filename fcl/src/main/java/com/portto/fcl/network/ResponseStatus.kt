package com.portto.fcl.network

import kotlinx.serialization.Serializable

@Serializable
enum class ResponseStatus {
    APPROVED,
    DECLINED,
    PENDING,
}
