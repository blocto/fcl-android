package com.portto.fcl.provider.blocto

import androidx.annotation.WorkerThread
import com.portto.fcl.error.NullPointerException
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.authn.AccountProofData as FclAccountProofData
import com.portto.sdk.wallet.BloctoSDKError
import com.portto.sdk.wallet.flow.AccountProofData as BloctoAccountProofData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Parse message from [BloctoSDKError]
 *
 * @return readable error message
 */
fun BloctoSDKError.parseErrorMessage() = message.split("_").joinToString(" ")


/**
 * Map from [BloctoAccountProofData] to [FclAccountProofData]
 */
internal fun BloctoAccountProofData.mapToFclAccountProofData(): FclAccountProofData =
    FclAccountProofData(
        nonce = nonce ?: throw NullPointerException("None"),
        address = address,
        signatures = signatures?.map {
            CompositeSignature(
                address = it.address,
                keyId = it.keyId,
                signature = it.signature
            )
        } ?: throw NullPointerException("Signatures"))