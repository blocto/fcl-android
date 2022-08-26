package com.portto.fcl.provider.blocto.web

import android.content.Context
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.nftco.flow.sdk.bytesToHex
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
import com.portto.fcl.request.AuthzRequest
import com.portto.fcl.utils.FclError
import com.portto.fcl.utils.toDataClass
import com.portto.fcl.utils.toJsonObject

internal object BloctoWebMethod : BloctoMethod {
    override suspend fun authenticate(
        context: Context,
        accountProofData: AccountProofResolvedData?
    ): User? {
        val response = execHttpPost(
            url = getAuthnUrl(Fcl.isMainnet),
            data = accountProofData?.toJsonObject()
        )

        val authData = response.data?.toDataClass<AuthData>()
            ?: throw FclError.AccountNotFoundException()

        val accountProofService = authData.services?.find {
            it.type == ServiceType.ACCOUNT_PROOF
        }

        val signatures = accountProofService?.data?.signatures

        if (accountProofData != null && signatures.isNullOrEmpty())
            throw FclError.SignaturesNotFoundException()

        Fcl.currentUser = User(
            address = authData.address ?: throw FclError.AccountNotFoundException(),
            accountProofData = if (!signatures.isNullOrEmpty()) {
                val accountProofSignedData = accountProofService.data
                AccountProofData(
                    nonce = accountProofSignedData.nonce
                        ?: throw FclError.GeneralException("Nonce not found in account proof"),
                    address = accountProofSignedData.address
                        ?: throw FclError.GeneralException("Address not found in account proof"),
                    signatures = accountProofSignedData.signatures?.map {
                        CompositeSignature(it.address, it.keyId, it.signature)
                    } ?: throw FclError.GeneralException("Signatures not found in account proof")
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
        val service = Fcl.currentUser?.services?.find { it.type == ServiceType.USER_SIGNATURE }
            ?: throw FclError.ServiceNotFoundException()

        val response = execHttpPost(
            url = service.endpoint!!,
            params = service.params,
            data = mapOf("message" to message.trim().toByteArray().bytesToHex()).toJsonObject()
        )

        return response.data?.toDataClass<List<CompositeSignature>>()
            ?: throw FclError.SignaturesNotFoundException()
    }

    override suspend fun sendTransaction(
        context: Context,
        script: String,
        userAddress: String,
        args: List<FlowArgument>,
        limit: ULong,
        authorizers: List<FlowAddress>
    ): String {
        val result = AuthzRequest().send {
            cadence(script)
            args.forEach { arg(it.jsonCadence) }
            gasLimit(limit.toInt())
        }
        return result
    }
}