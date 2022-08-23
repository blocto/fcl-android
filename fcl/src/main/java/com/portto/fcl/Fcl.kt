package com.portto.fcl

import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.nftco.flow.sdk.cadence.Field
import com.portto.fcl.config.AppDetail
import com.portto.fcl.config.Config
import com.portto.fcl.config.Config.Option.*
import com.portto.fcl.config.Network
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.Result
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.Provider
import com.portto.fcl.utils.AppUtils
import com.portto.fcl.utils.FclError

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
            ?: throw FclError.UnspecifiedWalletProviderException()
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
     * @param authorizers Accounts authorizing the transaction to mutate their state
     * @return Transaction id
     */
    suspend fun mutate(
        cadence: String,
        arguments: List<FlowArgument>?,
        limit: ULong = 1000u,
        authorizers: List<FlowAddress>,
    ): Result<String> = try {
        val selectedProvider = config.selectedWalletProvider
            ?: throw FclError.UnspecifiedWalletProviderException()
        Result.Success(
            selectedProvider.mutate(
                cadence = cadence,
                args = arguments ?: emptyList(),
                limit = limit,
                authorizers = authorizers
            )
        )
    } catch (e: Exception) {
        Result.Failure(e)
    }

    suspend fun signUserMessage(message: String): Result<List<CompositeSignature>> {
        return try {
            currentUser ?: throw FclError.UnauthenticatedException()
            val selectedProvider = config.selectedWalletProvider
                ?: throw FclError.UnspecifiedWalletProviderException()
            Result.Success(selectedProvider.getUserSignature(message))
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}