package com.portto.fcl

import com.nftco.flow.sdk.cadence.Field
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
import com.portto.fcl.utils.AppUtils

object Fcl {
    val config: Config = Config

    val isMainnet: Boolean
        get() = config.env == Network.MAINNET

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

    /**
     * Query the Flow Blockchain
     * @param cadence Cadence script used to query Flow
     * @param arguments Arguments passed to cadence query
     * @return Cadence response Value from Flow contract
     */
    suspend fun query(cadence: String, arguments: List<Field<*>>? = null): Result<Any?> = try {
        Result.Success(AppUtils.query(cadence, arguments).value)
    } catch (e: Exception) {
        Result.Failure(e)
    }

    /**
     * Mutate the Flow Blockchain
     * @param cadence Cadence script used to mutate Flow
     * @param arguments Arguments passed to cadence transaction
     * @param limit Gas limit for the computation of the transaction
     * @return Transaction id
     */
    fun mutate(
        cadence: String,
        arguments: List<Field<*>>? = null,
        limit: Long = 1000L
    ): Result<String> {
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