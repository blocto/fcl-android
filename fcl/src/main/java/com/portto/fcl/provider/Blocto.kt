package com.portto.fcl.provider

import com.nftco.flow.sdk.FlowAddress
import com.portto.fcl.model.User
import com.portto.fcl.provider.Provider.ProviderInfo
import java.net.URL

object Blocto : Provider {
    override var user: User? = null

    override val info: ProviderInfo
        get() = ProviderInfo(
            "Blocto",
            "Best wallet to interact with Web3",
            "https://i.imgur.com/gxQL1yq.png"
        )

    override suspend fun authn(): User {
        this.user = User(address = FlowAddress("0x00000000"))
        TODO("Not yet implemented")
    }

    override suspend fun authz() {
        TODO("Not yet implemented")
    }
}