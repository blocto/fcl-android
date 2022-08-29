package com.portto.fcl.utils

import com.nftco.flow.sdk.*
import com.nftco.flow.sdk.cadence.*
import com.portto.fcl.Fcl
import com.portto.fcl.config.Network
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.authn.AccountProofData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object AppUtils {
    private const val FLOW_MAINNET_ENDPOINT = "access.mainnet.nodes.onflow.org"
    private const val FLOW_TESTNET_ENDPOINT = "access.devnet.nodes.onflow.org"

    internal val flowApi
        get() = Flow.newAccessApi(
            host = if (Fcl.isMainnet) FLOW_MAINNET_ENDPOINT else FLOW_TESTNET_ENDPOINT
        )

    suspend fun verifyAccountProof(
        appIdentifier: String,
        accountProofData: AccountProofData
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val contract = getAccountProofContactAddress(
                Fcl.config.env ?: throw FclError.UnspecifiedNetworkException()
            )
            val script = getVerifySignaturesScript(isAccountProof = true, contract = contract)

            val verifyMessage = encodeAccountProof(
                appIdentifier = appIdentifier,
                address = accountProofData.address,
                nonce = accountProofData.nonce,
                includeDomainTag = false
            )

            query(
                queryScript = script,
                args = listOf(
                    AddressField(accountProofData.address),
                    StringField(verifyMessage),
                    ArrayField(accountProofData.signatures.map { IntNumberField(it.keyId) }),
                    ArrayField(accountProofData.signatures.map { StringField(it.signature) }),
                )
            ).value as Boolean
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun verifyUserSignatures(
        message: String,
        signatures: List<CompositeSignature>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val contract = getAccountProofContactAddress(
                Fcl.config.env ?: throw FclError.UnspecifiedNetworkException()
            )
            val script = getVerifySignaturesScript(isAccountProof = false, contract = contract)

            val verifyMessage = message.toByteArray(Charsets.UTF_8).bytesToHex()

            query(
                queryScript = script,
                args = listOf(
                    AddressField(signatures.first().address),
                    StringField(verifyMessage),
                    ArrayField(signatures.map { IntNumberField(it.keyId) }),
                    ArrayField(signatures.map { StringField(it.signature) }),
                )
            ).value as Boolean
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun query(
        queryScript: String,
        args: List<Field<*>>?
    ): Field<*> = withContext(Dispatchers.IO) {
        try {
            val result = flowApi.simpleFlowScript {
                script(queryScript)
                args?.let { arguments { it } }
            }
            result.jsonCadence
        } catch (exception: Exception) {
            throw exception
        }
    }

    internal suspend fun getAccount(address: String): FlowAccount = withContext(Dispatchers.IO) {
        flowApi.getAccountAtLatestBlock(FlowAddress(address))
            ?: throw  FclError.AccountNotFoundException()
    }

    internal suspend fun getLatestBlock(): FlowBlock = withContext(Dispatchers.IO) {
        flowApi.getLatestBlock(true)
    }

    private fun getAccountProofContactAddress(network: Network): String = when (network) {
        Network.TESTNET -> "0x74daa6f9c7ef24b1"
        Network.MAINNET -> "0xb4b82a1c9d21d284"
        Network.LOCAL, Network.CANARYNET -> throw FclError.UnsupportedNetworkException(network)
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
