package com.portto.fcl.provider.blocto

import com.portto.fcl.model.CompositeSignature as FclCompositeSignature
import com.portto.sdk.wallet.BloctoSDKError
import com.portto.sdk.wallet.flow.CompositeSignature as BloctoCompositeSignature
import com.portto.fcl.model.authn.AccountProofData as FclAccountProofData
import com.portto.sdk.wallet.flow.AccountProofData as BloctoAccountProofData

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
        nonce = requireNotNull(nonce) { "Nonce can not be null" },
        address = address,
        signatures = requireNotNull(signatures?.map {
            it.mapToFclCompositeSignature()
        }) { "Signatures can not be null" })

/**
 * Map from [BloctoCompositeSignature] to [FclCompositeSignature]
 */
internal fun BloctoCompositeSignature.mapToFclCompositeSignature(): FclCompositeSignature =
    FclCompositeSignature(
        address = address,
        keyId = keyId,
        signature = signature
    )