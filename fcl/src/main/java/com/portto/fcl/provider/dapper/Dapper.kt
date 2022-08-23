package com.portto.fcl.provider.dapper

import android.util.Log
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.portto.fcl.Fcl
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofData
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.model.service.ServiceType
import com.portto.fcl.network.execHttpPost
import com.portto.fcl.provider.*
import com.portto.fcl.provider.Provider.ProviderInfo
import com.portto.fcl.utils.FclError
import com.portto.fcl.utils.toJsonObject

object Dapper : Provider {
    override val id: Int = PROVIDER_DAPPER_ID

    override var user: User? = null

    override val info: ProviderInfo
        get() = ProviderInfo(
            PROVIDER_DAPPER_TITLE,
            PROVIDER_DAPPER_DESC,
            PROVIDER_DAPPER_ICON
        )

    override suspend fun authn(accountProofResolvedData: AccountProofResolvedData?) {
        val authnUrl = "https://dapper-http-post.vercel.app/api/authn"
        val response = execHttpPost(
            url = authnUrl,
            data = accountProofResolvedData?.toJsonObject()
        )

        Log.d("Test", "response: $response")
        val accountProofService = response.data?.services?.find {
            it.type == ServiceType.ACCOUNT_PROOF
        }

        val hasSignatures = !accountProofService?.data?.signatures.isNullOrEmpty()

        if (accountProofResolvedData != null && !hasSignatures) throw Error("Unable to fetch signatures.")

        Fcl.currentUser = User(
            address = response.data?.address ?: throw FclError.AccountNotFoundException(),
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
    }

    override suspend fun getUserSignature(message: String): List<CompositeSignature> {
        TODO("Not yet implemented")
    }

    override suspend fun mutate(
        cadence: String,
        args: List<FlowArgument>,
        limit: ULong,
        authorizers: List<FlowAddress>,
    ): String {
        TODO("Not yet implemented")
    }
}
