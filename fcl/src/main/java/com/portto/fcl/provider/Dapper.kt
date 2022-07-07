package com.portto.fcl.provider

import com.portto.fcl.model.User
import com.portto.fcl.provider.Provider.ProviderInfo
import java.net.URL

object Dapper : Provider {
    override var user: User? = null

    override val info: ProviderInfo
        get() = ProviderInfo(
            "Dapper",
            "Wallet created by Dapper Lab",
            "https://i.imgur.com/L1dgOKn.png"
        )

    override suspend fun authn(): User {
        TODO("Not yet implemented")
    }

    override suspend fun authz() {
        TODO("Not yet implemented")
    }
}