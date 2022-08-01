package com.portto.fcl

import com.portto.fcl.config.AppDetail
import com.portto.fcl.config.Config
import com.portto.fcl.config.Config.Option.*
import com.portto.fcl.config.Network
import com.portto.fcl.error.AuthenticationException
import com.portto.fcl.error.UnspecifiedWalletProviderException
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.Result
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.Provider

object Fcl {
    val config: Config = Config

    val isMainnet: Boolean = config.env == Network.MAINNET

    var currentUser: User? = null

    fun init(env: Network, appDetail: AppDetail, supportedWallets: List<Provider>): Config =
        config.apply {
            put(Env(env))
            put(App(appDetail))
            put(WalletProviders(supportedWallets))
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

    suspend fun signUserMessage(message: String): Result<List<CompositeSignature>> {
        return try {
            currentUser ?: throw AuthenticationException()
            val selectedProvider = config.selectedWalletProvider
                ?: throw UnspecifiedWalletProviderException()
            Result.Success(selectedProvider.getUserSignature(message))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}