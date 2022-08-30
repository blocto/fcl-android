package com.portto.fcl.provider.blocto

import android.content.Context
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData

internal interface BloctoMethod {
    suspend fun authenticate(
        context: Context,
        accountProofData:
        AccountProofResolvedData?
    ): User?

    suspend fun signUserMessage(
        context: Context,
        userAddress: String,
        message: String
    ): List<CompositeSignature>

    suspend fun sendTransaction(
        context: Context,
        script: String,
        userAddress: String,
        args: List<FlowArgument>,
        limit: ULong,
        authorizers: List<FlowAddress>
    ): String
}