package com.portto.fcl.model

/**
 *
 * Blocto App Identifier
 *  - testnet dashboard: https://developers-staging.blocto.app/
 *  - mainnet dashboard: https://developers.blocto.app/
 */
sealed class Origin(val value: String) {
    class Domain(url: String) : Origin(url)

    class Blocto(appIdentifier: String) : Origin(appIdentifier)
}