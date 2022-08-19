package com.portto.fcl.provider.blocto

import com.nftco.flow.sdk.*
import com.portto.fcl.Fcl
import com.portto.fcl.lifecycle.LifecycleObserver
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.*
import com.portto.fcl.provider.Provider.ProviderInfo
import com.portto.fcl.provider.blocto.Blocto.Companion.getInstance
import com.portto.fcl.provider.blocto.native.BloctoNativeMethod
import com.portto.fcl.utils.FclError
import com.portto.sdk.core.BloctoSDK
import com.portto.fcl.model.CompositeSignature as FclCompositeSignature

/**
 * Blocto Wallet Provider
 *
 * Usage: [getInstance] to init Blocto as a wallet provider
 * @param bloctoAppId the Blocto app identifier. For more info, check https://docs.blocto.app/blocto-sdk/register-app-id
 * @param isDebug indicator of the flow network. Set to true to connect testnet; false to mainnet
 */
class Blocto(bloctoAppId: String, isDebug: Boolean) : Provider {
    override val id: Int = PROVIDER_BLOCTO_ID

    override var user: User? = null

    override val info: ProviderInfo
        get() = ProviderInfo(
            PROVIDER_BLOCTO_TITLE,
            PROVIDER_BLOCTO_DESC,
            PROVIDER_BLOCTO_ICON
        )

    init {
        BloctoSDK.init(appId = bloctoAppId, debug = isDebug)
    }

    override suspend fun authn(accountProofResolvedData: AccountProofResolvedData?) {
        val context = LifecycleObserver.context() ?: throw Exception("Context is required")
        val user = BloctoNativeMethod.authenticate(context, accountProofResolvedData)
        Fcl.currentUser = user
    }

    override suspend fun getUserSignature(message: String): List<FclCompositeSignature> {
        val context = LifecycleObserver.context() ?: throw Exception("Context is required")
        val user = Fcl.currentUser ?: throw FclError.AuthenticationException()
        return BloctoNativeMethod.signUserMessage(context, user.address, message)
    }

    override suspend fun mutate(
        cadence: String,
        args: List<FlowArgument>,
        limit: ULong,
        authorizers: List<FlowAddress>,
    ): String {
        val context = LifecycleObserver.context() ?: throw Exception("Context is required")

        val user = Fcl.currentUser ?: throw FclError.AuthenticationException()

        return BloctoNativeMethod.sendTransaction(
            context = context, userAddress = user.address,
            script = cadence,
            args = args,
            limit = limit,
            authorizers = authorizers,
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: Blocto? = null

        @Synchronized
        fun getInstance(bloctoAppId: String, isDebug: Boolean) =
            INSTANCE ?: Blocto(bloctoAppId, isDebug).also { INSTANCE = it }
    }
}