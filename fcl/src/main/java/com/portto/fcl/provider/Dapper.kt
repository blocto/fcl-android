package com.portto.fcl.provider

import com.portto.fcl.model.User
import com.portto.fcl.provider.Provider.ProviderInfo

object Dapper : Provider {
    override var user: User? = null

    override val info: ProviderInfo
        get() = ProviderInfo("Dapper", "", null)

    override suspend fun authn(): User {
        TODO("Not yet implemented")
    }

    override suspend fun authz() {
        TODO("Not yet implemented")
    }
}