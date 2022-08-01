package com.portto.fcl.provider

import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData

interface Provider {
    val id: Int

    var user: User?

    val info: ProviderInfo

    suspend fun authn(accountProofResolvedData: AccountProofResolvedData? = null)

    suspend fun authz(): String

    suspend fun getUserSignature(message: String): List<CompositeSignature>

    data class ProviderInfo(
        val title: String,
        val description: String,
        val icon: String?,
    )
}