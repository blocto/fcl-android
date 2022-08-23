package com.portto.fcl.provider.blocto.web

import android.content.Context
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.portto.fcl.Fcl
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.PollingResponse
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofData
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.model.service.ServiceType
import com.portto.fcl.network.FclClient
import com.portto.fcl.network.NetworkUtils.openAuthenticationWebView
import com.portto.fcl.network.NetworkUtils.polling
import com.portto.fcl.network.NetworkUtils.repeatWhen
import com.portto.fcl.network.ResponseStatus
import com.portto.fcl.provider.blocto.BloctoMethod
import com.portto.fcl.provider.blocto.web.BloctoWebUtils.getWebAuthnUrl
import com.portto.fcl.utils.FclError

object BloctoWebMethod : BloctoMethod {
    override suspend fun authenticate(
        context: Context,
        accountProofData: AccountProofResolvedData?
    ): User? {
        val body = accountProofData?.run {
            mapOf(
                "accountProofIdentifier" to accountProofData.appIdentifier,
                "accountProofNonce" to accountProofData.nonce
            )
        }.orEmpty()
        val pollingResponse =
            FclClient.authService.executePost(getWebAuthnUrl(Fcl.isMainnet), body)

        val updates = pollingResponse.updates ?: throw Error("No updates from polling response")

        pollingResponse.openAuthenticationWebView()

        var authnResponse: PollingResponse? = null

        repeatWhen(predicate = { (authnResponse == null || authnResponse?.status == ResponseStatus.PENDING) }) {
            authnResponse = polling(updates)
        }

        if (authnResponse?.status == ResponseStatus.DECLINED) throw FclError.UserDeniedException()

        val accountProofService = authnResponse?.data?.services?.find {
            it.type == ServiceType.ACCOUNT_PROOF
        }

        val hasSignatures = !accountProofService?.data?.signatures.isNullOrEmpty()

        if (accountProofData != null && !hasSignatures) throw Error("Unable to fetch signatures")

        Fcl.currentUser = User(
            address = authnResponse?.data?.address ?: throw FclError.AccountNotFoundException(),
            accountProofData = if (hasSignatures) {
                val accountProofSignedData = accountProofService?.data

                AccountProofData(
                    nonce = accountProofSignedData?.nonce!!,
                    address = accountProofSignedData.address!!,
                    signatures = accountProofSignedData.signatures!!.map {
                        CompositeSignature(it.address, it.keyId, it.signature)
                    }
                )
            } else null
        )

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