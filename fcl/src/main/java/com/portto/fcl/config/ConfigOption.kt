package com.portto.fcl.config

import com.portto.fcl.provider.Provider

/**
 */
sealed class ConfigOption {
    class Env(val value: NetworkEnv) : ConfigOption()
    class App(val value: AppInfo?) : ConfigOption()
    class WalletProviders(val value: List<Provider>) : ConfigOption()
    class SelectedWalletProvider(val value: Provider) : ConfigOption()
}