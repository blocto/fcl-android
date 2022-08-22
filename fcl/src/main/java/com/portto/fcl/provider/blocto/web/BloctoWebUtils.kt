package com.portto.fcl.provider.blocto.web


object BloctoWebUtils {
    private const val AUTHN_STAGING_URL = "https://flow-wallet-testnet.blocto.app/api/flow/authn"
    private const val AUTHN_PRODUCTION_URL = "https://flow-wallet.blocto.app/api/flow/authn"

    fun getWebAuthnUrl(isMainnet: Boolean): String {
        return if (isMainnet) AUTHN_PRODUCTION_URL else AUTHN_STAGING_URL
    }
}