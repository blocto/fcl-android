package com.portto.fcl.provider.blocto.web

import android.content.Context
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.portto.fcl.Fcl
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.network.PollingResponse
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.network.FclClient
import com.portto.fcl.network.NetworkUtils.RESPONSE_PENDING
import com.portto.fcl.network.NetworkUtils.polling
import com.portto.fcl.network.NetworkUtils.repeatWhen
import com.portto.fcl.provider.blocto.BloctoMethod
import com.portto.fcl.provider.blocto.web.BloctoWebUtils.getWebAuthnUrl

import kotlinx.coroutines.delay

object BloctoWebMethod : BloctoMethod {
    override suspend fun authenticate(
        context: Context,
        accountProofData: AccountProofResolvedData?
    ): User? {
        val queryData = accountProofData?.run {
            mapOf(
                "accountProofIdentifier" to accountProofData.appIdentifier,
                "accountProofNonce" to accountProofData.nonce
            )
        }.orEmpty()
        val pollingResponse =
            FclClient.authService.executePost(getWebAuthnUrl(Fcl.isMainnet), queryData)
        val updates = pollingResponse.updates ?: throw Error()
        pollingResponse.openAuthenticationWebView()

        var authnResponse: PollingResponse? = null
        repeatWhen(predicate = { (authnResponse == null || authnResponse?.status == RESPONSE_PENDING) }) {
            delay(1000)
            authnResponse = polling(updates)
        }

        Fcl.currentUser = User(address = authnResponse?.data?.address.orEmpty())

        return Fcl.currentUser
    }

    override suspend fun signUserMessage(
        context: Context,
        userAddress: String,
        message: String
    ): List<CompositeSignature> {
        TODO("Not yet implemented")
    }

    override suspend fun sendTransaction(
        context: Context,
        script: String,
        userAddress: String,
        args: List<FlowArgument>,
        limit: ULong,
        authorizers: List<FlowAddress>
    ): String {
        TODO("Not yet implemented")
    }
}