package com.portto.fcl

import com.portto.fcl.config.AppInfo
import com.portto.fcl.config.Config
import com.portto.fcl.config.ConfigOption
import com.portto.fcl.config.NetworkEnv
import com.portto.fcl.model.User
import com.portto.fcl.provider.Blocto
import com.portto.fcl.provider.Provider

object FCL {
    val config: Config = Config()

    var currentUser: User? = null
        private set

    val isMainnet: Boolean = config.env == NetworkEnv.MAINNET

    fun init(env: NetworkEnv, appInfo: AppInfo, supportedWallets: List<Provider>? = null): Config =
        config.apply {
            put(ConfigOption.Env(env))
            put(ConfigOption.App(appInfo))
            put(ConfigOption.WalletProvider(supportedWallets ?: listOf(Blocto)))
        }

    suspend fun authenticate() {
        val selectedProvider = config.selectedWalletProvider
        if (selectedProvider != null) selectedProvider.authn()
        else {
            TODO("Show ${config.supportedWallets} to the user")
        }
    }

    suspend fun unauthenticate() {
        currentUser = null
        TODO("Not yet implemented")
    }

    fun query() {
        TODO("Not yet implemented")
    }

    fun sendTx() {
        TODO("Not yet implemented")
    }
}