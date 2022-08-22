package com.portto.fcl.provider.dapper

import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.portto.fcl.Fcl
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.model.network.PollingResponse
import com.portto.fcl.network.FclClient
import com.portto.fcl.network.NetworkUtils.polling
import com.portto.fcl.network.NetworkUtils.repeatWhen
import com.portto.fcl.provider.*
import com.portto.fcl.provider.Provider.ProviderInfo
import kotlinx.coroutines.delay

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
        val pollingResponse = FclClient.authService.executePost("https://dapper-http-post.vercel.app/api/authn")

        val updates = pollingResponse.updates ?: throw Error()
        pollingResponse.openAuthenticationWebView()

        var authnResponse: PollingResponse? = null
        repeatWhen(predicate = { (authnResponse == null || authnResponse?.status == "PENDING") }) {
            delay(1000)
            authnResponse = polling(updates)
        }

        Fcl.currentUser = User(address = authnResponse?.data?.address.orEmpty())
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
