package com.portto.fcl.config

import com.portto.fcl.provider.Provider

/**
 * Copyright 2022 Portto, Inc.
 *
 */
class Config {
    var env: NetworkEnv? = null
        private set

    var appInfo: AppInfo? = null
        private set

    var supportedWallets: List<Provider> = listOf()
        private set

    var selectedWalletProvider: Provider? = null
        private set

    fun put(option: ConfigOption): Config = apply {
        when (option) {
            is ConfigOption.Env -> env = option.value
            is ConfigOption.App -> appInfo = option.value
            is ConfigOption.WalletProviders -> {
                option.value.let {
                    supportedWallets = it
                    if (it.size == 1) selectedWalletProvider = it.first()
                }
            }
            is ConfigOption.SelectedWalletProvider -> selectedWalletProvider = option.value
        }
    }
}
