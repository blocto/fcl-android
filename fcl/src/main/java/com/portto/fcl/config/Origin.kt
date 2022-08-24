package com.portto.fcl.config

sealed class Origin(val value: String) {
    class Domain(url: String) : Origin(url)

    /**
     * Blocto App Identifier for web
     *  - testnet dashboard: https://developers-staging.blocto.app/
     *  - mainnet dashboard: https://developers.blocto.app/
     */
    class Blocto(appIdentifier: String) : Origin(appIdentifier)
}