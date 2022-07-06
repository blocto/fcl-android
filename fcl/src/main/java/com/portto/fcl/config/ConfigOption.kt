package com.portto.fcl.config

import com.portto.fcl.provider.Provider
import java.net.URL

/**
 */
sealed class ConfigOption {
    class Env(val value: NetworkEnv) : ConfigOption()
    data class App(val value: AppInfo?) : ConfigOption()
    data class WalletProvider(val value: List<Provider>) : ConfigOption()
}

data class AppInfo(
    val title: String = "fcl sample",
    val iconUrl: URL? = null,
)

enum class NetworkEnv(val type: String) {
    LOCAL("local"),
    CANARYNET("canarynet"),
    TESTNET("testnet"),
    MAINNET("mainnet"),
}