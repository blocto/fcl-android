package com.portto.fcl.provider.blocto.web

import android.content.Context
import android.util.Log
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.portto.fcl.Fcl
import com.portto.fcl.model.AuthData
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofData
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.model.service.ServiceType
import com.portto.fcl.network.execHttpPost
import com.portto.fcl.provider.blocto.BloctoMethod
import com.portto.fcl.provider.blocto.web.BloctoWebUtils.getAuthnUrl
import com.portto.fcl.utils.FclError
import com.portto.fcl.utils.toDataClass
import com.portto.fcl.utils.toJsonObject

object BloctoWebMethod : BloctoMethod {
    override suspend fun authenticate(
        context: Context,
        accountProofData: AccountProofResolvedData?
    ): User? {
        val response = execHttpPost(
            url = getAuthnUrl(Fcl.isMainnet),
            data = accountProofData?.toJsonObject()
        )
        Log.d("Test", "response: $response")

        val authData = response.data?.toDataClass<AuthData>()
            ?: throw FclError.AccountNotFoundException()

        val accountProofService = authData.services.find {
            it.type == ServiceType.ACCOUNT_PROOF
        }

        val signatures = accountProofService?.data?.signatures

        if (accountProofData != null && signatures.isNullOrEmpty())
            throw Error("Unable to fetch signatures.")

        Fcl.currentUser = User(
            address = authData.address,
            accountProofData = if (!signatures.isNullOrEmpty()) {
                val accountProofSignedData = accountProofService.data
                AccountProofData(
                    nonce = accountProofSignedData.nonce!!,
                    address = accountProofSignedData.address!!,
                    signatures = accountProofSignedData.signatures!!.map {
                        CompositeSignature(it.address, it.keyId, it.signature)
                    }
                )
            } else null,
            services = authData.services
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