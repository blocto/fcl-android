package com.portto.fcl.sample.util

import com.portto.fcl.model.CompositeSignature


/**
 * Map Flow [CompositeSignature] to string for display
 */
fun List<CompositeSignature>.mapToString() = joinToString("\n\n") {
    "Address: ${it.address}\nKey ID: ${it.keyId}\nSignature: ${it.signature}"
}


/**
 * Get sample script for query
 */
fun getQuerySampleScript(isMainnet: Boolean): String = """
    import ValueDapp from ${if (isMainnet) MAINNET_SAMPLE_CONTRACT_ADDRESS else TESTNET_SAMPLE_CONTRACT_ADDRESS}

    pub fun main(): UFix64 {
        return ValueDapp.value
    }
""".trimIndent()

/**
 * Get sample script for mutate
 */
fun getMutateSampleScript(isMainnet: Boolean): String = """
    import ValueDapp from ${if (isMainnet) MAINNET_SAMPLE_CONTRACT_ADDRESS else TESTNET_SAMPLE_CONTRACT_ADDRESS}

    transaction(value: UFix64) {
        prepare(authorizer: AuthAccount) {
            ValueDapp.setValue(value)
        }
    }
""".trimIndent()