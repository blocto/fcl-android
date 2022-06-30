package com.portto.fcl

import androidx.annotation.WorkerThread
import com.nftco.flow.sdk.FlowAddress
import com.portto.fcl.config.Config
import com.portto.fcl.model.User
import com.portto.fcl.model.discovery.Service
import com.portto.fcl.network.FclApi

object FCL {
    val config = Config.init()

    var currentUser: User? = null
        private set

    fun config(
        appName: String? = null,
        appIconUrl: String? = null,
        accessNode: String? = null,
    ): Config = config.apply {
        appName?.let { put(Config.Key.APP_TITLE, it) }
        appIconUrl?.let { put(Config.Key.APP_ICON, it) }
        accessNode?.let { put(Config.Key.ACCESS_NODE_API, it) }
    }

    suspend fun discoverWallets(): List<Service> = FclApi.discoveryService.getWalletProviders()

    @WorkerThread
    fun authenticate() {
        currentUser = User(address = FlowAddress(""))
    }

    fun isMainnet(): Boolean = config.get(Config.Key.NETWORK) == "mainnet"
}