package com.portto.fcl.provider.blocto

import com.nftco.flow.sdk.*
import com.portto.fcl.Fcl
import com.portto.fcl.lifecycle.LifecycleObserver
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.*
import com.portto.fcl.provider.Provider.ProviderInfo
import com.portto.fcl.provider.blocto.Blocto.Companion.getInstance
import com.portto.fcl.utils.AppUtils.getAccount
import com.portto.fcl.utils.AppUtils.getLatestBlock
import com.portto.fcl.utils.FclError
import com.portto.sdk.core.BloctoSDK
import com.portto.sdk.flow.flow
import com.portto.sdk.wallet.BloctoSDKError
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.portto.fcl.model.CompositeSignature as FclCompositeSignature
import com.portto.sdk.wallet.flow.AccountProofData as BloctoAccountProofData
import com.portto.sdk.wallet.flow.CompositeSignature as BloctoCompositeSignature

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

        suspendCancellableCoroutine { cont ->
            val successCallback: (BloctoAccountProofData) -> Unit = {
                Fcl.currentUser = User(
                    address = it.address,
                    accountProofData =
                    if (accountProofResolvedData != null) it.mapToFclAccountProofData()
                    else null
                )
                cont.resume(Fcl.currentUser)
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
        val context = LifecycleObserver.context() ?: throw Exception("Context is required")
        val user = Fcl.currentUser ?: throw FclError.AuthenticationException()

        return suspendCancellableCoroutine { cont ->
            val successCallback: (List<BloctoCompositeSignature>) -> Unit = {
                cont.resume(it.map { signature -> signature.mapToFclCompositeSignature() })
            }
            val failureCallback: (BloctoSDKError) -> Unit = {
                cont.resumeWithException(Exception(it.parseErrorMessage()))
            }

            BloctoSDK.flow.signUserMessage(
                context = context,
                address = user.address,
                message = message.trim(),
                onSuccess = successCallback,
                onError = failureCallback,
            )
        }
    }

    override suspend fun mutate(cadence: String, args: List<FlowArgument>, limit: ULong): String {
        val context = LifecycleObserver.context() ?: throw Exception("Context is required")

        val user = Fcl.currentUser ?: throw FclError.AuthenticationException()

        val account = getAccount(user.address)

        val block = getLatestBlock()

        val cosignerKey = account.keys.find { it.weight == 999 && !it.revoked }
            ?: throw FclError.KeyNotFoundException()

        val proposalKey = FlowTransactionProposalKey(
            address = FlowAddress(user.address),
            keyIndex = cosignerKey.id,
            sequenceNumber = cosignerKey.sequenceNumber.toLong(),
        )

        val feePayerAddress = getFeePayerAddress()

        val transaction = FlowTransaction(
            script = FlowScript(script = cadence),
            arguments = args,
            referenceBlockId = block.id,
            gasLimit = limit.toLong(),
            proposalKey = proposalKey,
            payerAddress = FlowAddress(feePayerAddress),
            authorizers = listOf(FlowAddress(user.address))
        )

        return suspendCancellableCoroutine { cont ->
            val successCallback: (String) -> Unit = {
                cont.resume(it)
            }
            val failureCallback: (BloctoSDKError) -> Unit = {
                cont.resumeWithException(Exception(it.parseErrorMessage()))
            }

            BloctoSDK.flow.sendTransaction(
                context = context,
                address = user.address,
                transaction = transaction.canonicalTransaction.bytesToHex(),
                onSuccess = successCallback,
                onError = failureCallback,
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: Blocto? = null

        @Synchronized
        fun getInstance(bloctoAppId: String, isDebug: Boolean) =
            INSTANCE ?: Blocto(bloctoAppId, isDebug).also { INSTANCE = it }
    }
}