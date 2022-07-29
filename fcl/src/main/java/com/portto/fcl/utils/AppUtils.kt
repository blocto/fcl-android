package com.portto.fcl.utils

import com.nftco.flow.sdk.Flow
import com.nftco.flow.sdk.cadence.AddressField
import com.nftco.flow.sdk.cadence.ArrayField
import com.nftco.flow.sdk.cadence.IntNumberField
import com.nftco.flow.sdk.cadence.StringField
import com.nftco.flow.sdk.simpleFlowScript
import com.portto.fcl.Fcl
import com.portto.fcl.config.NetworkEnv
import com.portto.fcl.error.UnspecifiedNetworkException
import com.portto.fcl.error.UnsupportedNetworkException
import com.portto.fcl.model.authn.AccountProofData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object AppUtils {
    suspend fun verifyAccountProof(
        appIdentifier: String,
        accountProofData: AccountProofData
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val contract =
                getAccountProofContactAddress(Fcl.config.env ?: throw UnspecifiedNetworkException())
            val script = getVerifySignaturesScript(isAccountProof = true, contract = contract)

            val verifyMessage = encodeAccountProof(
                appIdentifier = appIdentifier,
                address = accountProofData.address,
                nonce = accountProofData.nonce,
                includeDomainTag = false
            )

            val result = getFlowApi(Fcl.isMainnet).simpleFlowScript {
                script(script)
                arg(AddressField(accountProofData.address))
                arg(StringField(verifyMessage))
                arg(ArrayField(accountProofData.signatures.map { IntNumberField(it.keyId) }))
                arg(ArrayField(accountProofData.signatures.map { StringField(it.signature) }))
            }
            result.jsonCadence.value as Boolean
        } catch (exception: Exception) {
            throw exception
        }
    }

//    fun verifyUserSignatures(message: String, signatures: List<CompositeSignature>) {
//        val contract =
//            getAccountProofContactAddress(Fcl.config.env ?: throw UnspecifiedNetworkException())
//        val script = getVerifySignaturesScript(isAccountProof = false, contract = contract)
//
//    }

    private const val FLOW_MAINNET_ENDPOINT = "access.mainnet.nodes.onflow.org"
    private const val FLOW_TESTNET_ENDPOINT = "access.devnet.nodes.onflow.org"

    private fun getFlowApi(isMainNet: Boolean) = Flow.newAccessApi(
        host = if (isMainNet) FLOW_MAINNET_ENDPOINT else FLOW_TESTNET_ENDPOINT,
        port = 9000
    )

    private fun getAccountProofContactAddress(network: NetworkEnv): String = when (network) {
        NetworkEnv.TESTNET -> "0x74daa6f9c7ef24b1"
        NetworkEnv.MAINNET -> "0xb4b82a1c9d21d284"
        NetworkEnv.LOCAL, NetworkEnv.CANARYNET -> throw UnsupportedNetworkException(network)
    }

    private fun getVerifySignaturesScript(isAccountProof: Boolean, contract: String): String {
        val verifyFunction = if (isAccountProof) "verifyAccountProofSignatures"
        else "verifyUserSignatures"

        return """
        import FCLCrypto from $contract

        pub fun main(
            address: Address,
            message: String,
            keyIndices: [Int],
        signatures: [String]
        ): Bool {
            return FCLCrypto.$verifyFunction(address: address, message: message, keyIndices: keyIndices, signatures: signatures)
        }
        """.trimIndent()
    }
}
