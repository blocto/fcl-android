package com.portto.fcl.provider.blocto

import android.content.Context
import android.content.pm.PackageManager
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.portto.fcl.Fcl
import com.portto.fcl.lifecycle.LifecycleObserver.Companion.requireContext
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.*
import com.portto.fcl.provider.Provider.ProviderInfo
import com.portto.fcl.provider.blocto.Blocto.Companion.getInstance
import com.portto.fcl.provider.blocto.native.BloctoNativeMethod
import com.portto.fcl.provider.blocto.web.BloctoWebMethod
import com.portto.fcl.utils.FclError
import com.portto.sdk.core.BloctoSDK
import com.portto.fcl.model.CompositeSignature as FclCompositeSignature

/**
 * Blocto Wallet Provider
 *
 * Usage: [getInstance] to init Blocto as a wallet provider
 * @param bloctoAppId the Blocto app identifier. For more info, check https://docs.blocto.app/blocto-sdk/register-app-id
 */
class Blocto(bloctoAppId: String) : Provider {
    override val id: Int = PROVIDER_BLOCTO_ID

    override var user: User? = null

    override val info: ProviderInfo
        get() = ProviderInfo(
            PROVIDER_BLOCTO_TITLE,
            PROVIDER_BLOCTO_DESC,
            PROVIDER_BLOCTO_ICON
        )

    init {
        BloctoSDK.init(appId = bloctoAppId, debug = Fcl.isMainnet)
    }

    override suspend fun authn(accountProofResolvedData: AccountProofResolvedData?) {
        val context = requireContext()

        val user: User? = isAppInstalled(context = context, isMainnet = Fcl.isMainnet)
            .getCaller()
            .authenticate(context, accountProofResolvedData)

        Fcl.currentUser = user
    }

    override suspend fun getUserSignature(message: String): List<FclCompositeSignature> {
        val user = Fcl.currentUser ?: throw FclError.UnauthenticatedException()
        val context = requireContext()
        return isAppInstalled(context = context, isMainnet = Fcl.isMainnet)
            .getCaller()
            .signUserMessage(requireContext(), user.address, message)
    }

    override suspend fun mutate(
        cadence: String,
        args: List<FlowArgument>,
        limit: ULong,
        authorizers: List<FlowAddress>,
    ): String {
        val context = requireContext()
        val user = Fcl.currentUser ?: throw FclError.UnauthenticatedException()

        return isAppInstalled(context = context, isMainnet = Fcl.isMainnet)
            .getCaller()
            .sendTransaction(
                context = context,
                userAddress = user.address,
                script = cadence,
                args = args,
                limit = limit,
                authorizers = authorizers,
            )
    }

    companion object {
        private const val BLOCTO_PRODUCTION_APP_ID = "com.portto.blocto"
        private const val BLOCTO_STAGING_APP_ID = "com.portto.blocto.staging"

        @Volatile
        private var INSTANCE: Blocto? = null

        @Synchronized
        fun getInstance(bloctoAppId: String) =
            INSTANCE ?: Blocto(bloctoAppId).also { INSTANCE = it }

        /**
         * Check if blocto app is installed
         * @return true if its installed; Otherwise false
         */
        private fun isAppInstalled(context: Context, isMainnet: Boolean) =
            try {
                context.packageManager.getPackageInfo(
                    if (isMainnet) BLOCTO_PRODUCTION_APP_ID else BLOCTO_STAGING_APP_ID, 0
                )
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }

        /**
         * Get the corresponding methods
         * @return native methods if app is installed; Otherwise, web methods
         */
        private fun Boolean.getCaller(): BloctoMethod =
            if (this) BloctoNativeMethod else BloctoWebMethod
    }
}