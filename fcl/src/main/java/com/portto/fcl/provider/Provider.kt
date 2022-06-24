package com.portto.fcl.provider

import java.net.URL

interface Provider {
    val name: String
    val endpoint: URL
}

data class WalletProvider(override val name: String, override val endpoint: URL) : Provider


val DAPPER = WalletProvider("Dapper", URL("https://dapper-http-post.vercel.app/api/"))
val BLOCTO = WalletProvider("Blocto", URL("https://flow-wallet.blocto.app/api/flow/"))