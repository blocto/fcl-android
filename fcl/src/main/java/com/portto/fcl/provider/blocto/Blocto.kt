package com.portto.fcl.provider.blocto

import com.portto.fcl.Fcl
import com.portto.fcl.lifecycle.LifecycleObserver
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.model.CompositeSignature as FclCompositeSignature
import com.portto.fcl.provider.Provider
import com.portto.fcl.provider.Provider.ProviderInfo
import com.portto.fcl.provider.blocto.Blocto.Companion.getInstance
import com.portto.fcl.utils.PROVIDER_BLOCTO_DESC
import com.portto.fcl.utils.PROVIDER_BLOCTO_ICON
import com.portto.fcl.utils.PROVIDER_BLOCTO_ID
import com.portto.fcl.utils.PROVIDER_BLOCTO_TITLE
import com.portto.sdk.core.BloctoSDK
import com.portto.sdk.flow.flow
import com.portto.sdk.wallet.BloctoSDKError
import com.portto.sdk.wallet.flow.AccountProofData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
        val context = LifecycleObserver.context() ?: throw Exception("Context not found")

        suspendCancellableCoroutine { cont ->
            val successCallback: (AccountProofData) -> Unit = {
                Fcl.currentUser =
                    User(address = it.address,
                        accountProofSignatures = it.signatures?.map { sig ->
                            FclCompositeSignature(
                                address = sig.address,
                                keyId = sig.keyId,
                                signature = sig.signature
                            )
                        })
                cont.resume(it)
            }
            val failureCallback: (BloctoSDKError) -> Unit = {
                cont.resumeWithException(Exception(it.parseErrorMessage()))
            }

            BloctoSDK.flow.authenticate(
                context = context,
                flowAppId = accountProofResolvedData?.appIdentifier,
                flowNonce = accountProofResolvedData?.nonce,
                onSuccess = successCallback,
                onError = failureCallback
            )
        }
    }

    override suspend fun getUserSignature(message: String): List<FclCompositeSignature> {
        TODO("Not yet implemented")
    }

    override suspend fun authz(): String {
        TODO("Not yet implemented")
    }

    companion object {
        @Volatile
        private var INSTANCE: Blocto? = null

        @Synchronized
        fun getInstance(bloctoAppId: String, isDebug: Boolean) =
            INSTANCE ?: Blocto(bloctoAppId, isDebug).also { INSTANCE = it }
    }
}