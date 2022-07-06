package com.portto.fcl.provider

import com.nftco.flow.sdk.FlowAddress
import com.portto.fcl.model.User
import com.portto.fcl.provider.Provider.ProviderInfo

object Blocto : Provider {
    override var user: User? = null

    override val info: ProviderInfo
        get() = ProviderInfo("Blocto", "", null)

    override suspend fun authn(): User {
        this.user = User(address = FlowAddress("0x00000000"))
        TODO("Not yet implemented")
    }

    override suspend fun authz() {
        TODO("Not yet implemented")
    }
}