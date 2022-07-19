package com.portto.fcl.provider

import com.portto.fcl.model.User

interface Provider {
    var user: User?

    val info: ProviderInfo

    suspend fun authn(): User

    suspend fun authz()

    data class ProviderInfo(
        val title: String,
        val description: String,
        val icon: String?,
    )
}