package com.portto.fcl.config

import com.portto.fcl.provider.Blocto
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

    var supportedWallets: List<Provider>? = null
        private set

    var selectedWalletProvider: Provider? = null
        private set

    fun put(option: ConfigOption): Config = apply {
        when (option) {
            is ConfigOption.Env -> env = option.value
            is ConfigOption.App -> appInfo = option.value
            is ConfigOption.WalletProvider -> {
                option.value.let {
                    supportedWallets = it
                    if (it.isEmpty()) selectedWalletProvider = Blocto
                    else if (it.size == 1) selectedWalletProvider = it.first()
                }
            }
        }
    }


}
