package com.portto.fcl.provider.blocto.web

internal object BloctoWebUtils {
    private const val AUTHN_STAGING_URL = "https://wallet-v2-dev.blocto.app/api/flow/dapp/authn"
    private const val AUTHN_PRODUCTION_URL = "https://wallet-v2.blocto.app/api/flow/dapp/authn"

    fun getAuthnUrl(isMainnet: Boolean): String {
        return if (isMainnet) AUTHN_PRODUCTION_URL else AUTHN_STAGING_URL
    }
}
