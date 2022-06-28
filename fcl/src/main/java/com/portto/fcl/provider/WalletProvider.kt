package com.portto.fcl.provider

import java.net.URL

enum class WalletProvider(override val title: String, override val endpoint: URL) : Provider {
    BLOCTO("Blocto", URL("")),
    DAPPER("Dapper", URL(""))
}


