package com.portto.fcl

import com.portto.fcl.config.AppInfo
import com.portto.fcl.config.Config
import com.portto.fcl.config.ConfigOption
import com.portto.fcl.config.NetworkEnv
import com.portto.fcl.error.UnspecifiedWalletProviderException
import com.portto.fcl.model.Result
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.Provider

object Fcl {
    val config: Config = Config()

    val isMainnet: Boolean = config.env == NetworkEnv.MAINNET

    var currentUser: User? = null

    fun init(env: NetworkEnv, appInfo: AppInfo, supportedWallets: List<Provider>): Config =
        config.apply {
            put(ConfigOption.Env(env))
            put(ConfigOption.App(appInfo))
            put(ConfigOption.WalletProviders(supportedWallets))
        }

    suspend fun authenticate(accountProofData: AccountProofResolvedData?): Result<String> {
        val selectedProvider = config.selectedWalletProvider
            ?: throw UnspecifiedWalletProviderException()
        return try {
            selectedProvider.authn(accountProofData)
            val address = currentUser?.address
                ?: throw Exception("Error while authenticating")
            Result.Success(address)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    fun unauthenticate() {
        currentUser = null
    }

    fun query() {
        TODO("Not yet implemented")
    }

    fun sendTx() {
        TODO("Not yet implemented")
    }

    fun signUserMessage() {
//        val user = currentUser ?: throw NotAuthenticatedException("User not found")
        TODO("Not yet implemented")
    }
}