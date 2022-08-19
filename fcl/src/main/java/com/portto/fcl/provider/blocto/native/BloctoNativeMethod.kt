package com.portto.fcl.provider.blocto.native

import android.content.Context
import com.nftco.flow.sdk.*
import com.portto.fcl.model.CompositeSignature as FclCompositeSignature
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.blocto.*
import com.portto.fcl.provider.blocto.mapToFclAccountProofData
import com.portto.fcl.provider.blocto.mapToFclCompositeSignature
import com.portto.fcl.utils.AppUtils
import com.portto.fcl.utils.FclError
import com.portto.sdk.core.BloctoSDK
import com.portto.sdk.flow.flow
import com.portto.sdk.wallet.BloctoSDKError
import com.portto.sdk.wallet.flow.AccountProofData
import com.portto.sdk.wallet.flow.CompositeSignature as BloctoCompositeSignature
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal object BloctoNativeMethod : BloctoMethod {

    override suspend fun authenticate(
        context: Context,
        accountProofData:
        AccountProofResolvedData?
    ): User? = suspendCancellableCoroutine { cont ->
        val successCallback: (AccountProofData) -> Unit = {
            val user = User(
                address = it.address,
                accountProofData =
                if (accountProofData != null) it.mapToFclAccountProofData()
                else null
            )
            cont.resume(user)
        }
        val failureCallback: (BloctoSDKError) -> Unit = {
            cont.resumeWithException(Exception(it.parseErrorMessage()))
        }

        BloctoSDK.flow.authenticate(
            context = context,
            flowAppId = accountProofData?.appIdentifier,
            flowNonce = accountProofData?.nonce,
            onSuccess = successCallback,
            onError = failureCallback
        )
    }

    override suspend fun signUserMessage(
        context: Context,
        userAddress: String,
        message: String
    ): List<FclCompositeSignature> {
        return suspendCancellableCoroutine { cont ->
            val successCallback: (List<BloctoCompositeSignature>) -> Unit = {
                cont.resume(it.map { signature -> signature.mapToFclCompositeSignature() })
            }
            val failureCallback: (BloctoSDKError) -> Unit = {
                cont.resumeWithException(Exception(it.parseErrorMessage()))
            }

            BloctoSDK.flow.signUserMessage(
                context = context,
                address = userAddress,
                message = message.trim(),
                onSuccess = successCallback,
                onError = failureCallback,
            )
        }
    }

    override suspend fun sendTransaction(
        context: Context,
        script: String,
        userAddress: String,
        args: List<FlowArgument>,
        limit: ULong,
        authorizers: List<FlowAddress>
    ): String {
        val account = AppUtils.getAccount(userAddress)

        val block = AppUtils.getLatestBlock()

        val cosignerKey = account.keys.find { it.weight == 999 && !it.revoked }
            ?: throw FclError.KeyNotFoundException()

        val proposalKey = FlowTransactionProposalKey(
            address = FlowAddress(userAddress),
            keyIndex = cosignerKey.id,
            sequenceNumber = cosignerKey.sequenceNumber.toLong(),
        )

        val feePayerAddress = getFeePayerAddress()

        val transaction = FlowTransaction(
            script = FlowScript(script),
            arguments = args,
            referenceBlockId = block.id,
            gasLimit = limit.toLong(),
            proposalKey = proposalKey,
            payerAddress = FlowAddress(feePayerAddress),
            authorizers = authorizers
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
                address = userAddress,
                transaction = transaction.canonicalTransaction.bytesToHex(),
                onSuccess = successCallback,
                onError = failureCallback,
            )
        }
    }

}