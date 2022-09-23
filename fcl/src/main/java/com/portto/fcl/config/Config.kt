package com.portto.fcl.config

import com.portto.fcl.provider.Provider

/**
 * Copyright 2022 Portto, Inc.
 *
 */
object Config {
    var env: Network? = null
        private set

    var appDetail: AppDetail? = null
        private set

    var supportedWallets: List<Provider> = listOf()
        private set

    var selectedWalletProvider: Provider? = null
        private set

    /**
     * key: substitute used to replace address
     * value: the address
     */
    var addressReplacement: Map<String, String> = emptyMap()
        private set

    fun put(option: Option): Config = apply {
        when (option) {
            is Option.Env -> env = option.value
            is Option.App -> appDetail = option.value
            is Option.WalletProviders -> {
                option.value.let {
                    supportedWallets = it
                    if (it.size == 1) selectedWalletProvider = it.first()
                }
            }
            is Option.SelectedWalletProvider -> selectedWalletProvider = option.value
            is Option.AddressReplacement -> addressReplacement = option.value
        }
    }

    fun clear() {
        env = null
        appDetail = null
        supportedWallets = emptyList()
        selectedWalletProvider = null
        addressReplacement = emptyMap()
    }

    sealed class Option {
        class Env(val value: Network) : Option()
        class App(val value: AppDetail?) : Option()
        class WalletProviders(val value: List<Provider>) : Option()
        class SelectedWalletProvider(val value: Provider) : Option()
        class AddressReplacement(val value: Map<String, String>) : Option()
    }
}
