package com.portto.fcl

import android.util.Log
import androidx.annotation.WorkerThread
import com.nftco.flow.sdk.FlowAddress
import com.portto.fcl.config.Config
import com.portto.fcl.config.Config.Companion.Key.*
import com.portto.fcl.model.User
import com.portto.fcl.model.discovery.Provider
import com.portto.fcl.network.FclApi

object FCL {
    val config = Config()

    var currentUser: User? = null
        private set

    fun config(
        appName: String? = null,
        appIconUrl: String? = null,
        accessNode: String? = null,
    ): Config = config.apply {
        appName?.let { put(APP_TITLE, it) }
        appIconUrl?.let { put(APP_ICON, it) }
        accessNode?.let { put(ACCESS_NODE_API, it) }
    }

    suspend fun discoverWallets(): List<Provider> {
        Log.d("Test", "wallets: ${FclApi.discoveryService.getWalletProviders()}")
        return emptyList()
    }

    @WorkerThread
    fun authenticate() {
        currentUser = User(address = FlowAddress(""))
    }

    fun isMainnet(): Boolean = config.get(NETWORK) == "mainnet"
}