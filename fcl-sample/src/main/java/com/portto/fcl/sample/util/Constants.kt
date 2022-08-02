package com.portto.fcl.sample.util

/**
 * Visit developer dashboard to acquire your blocto App ID
 *  testnet: https://developers-staging.blocto.app/
 *  mainnet: https://developers.blocto.app/
 *  */
const val BLOCTO_APP_ID = "f10a3ec7-bfee-47aa-8c96-90024dffc9ec"

const val FLOW_APP_IDENTIFIER = "Awesome App (v0.0)"
const val FLOW_NONCE = "75f8587e5bd5f9dcc9909d0dae1f0ac5814458b2ae129620502cb936fde7120a"

const val SCRIPT_QUERY_SAMPLE = """
pub fun main(): Int {
    return 1 + 2
}
"""

const val SCRIPT = """
transaction {
    execute {
        log("A transaction happened")
    }
}
        """