package com.portto.fcl.provider

import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData

interface Provider {
    val id: Int

    var user: User?

    val info: ProviderInfo

    suspend fun authn(accountProofResolvedData: AccountProofResolvedData? = null)

    suspend fun getUserSignature(message: String): List<CompositeSignature>

    suspend fun mutate(
        cadence: String,
        args: List<FlowArgument>,
        limit: ULong,
        authorizers: List<FlowAddress>
    ): String

    data class ProviderInfo(
        val title: String,
        val description: String,
        val icon: String?,
    )
}