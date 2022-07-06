package com.portto.fcl.config

import com.portto.fcl.provider.Provider

/**
 */
sealed class ConfigOption {
    class Env(val value: NetworkEnv) : ConfigOption()
    data class App(val value: AppInfo?) : ConfigOption()
    data class WalletProvider(val value: List<Provider>) : ConfigOption()
}