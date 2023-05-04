package com.portto.fcl

import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.nftco.flow.sdk.FlowTransactionResult
import com.nftco.flow.sdk.cadence.Field
import com.portto.fcl.config.AppDetail
import com.portto.fcl.config.Config
import com.portto.fcl.config.Config.Option.App
import com.portto.fcl.config.Config.Option.Env
import com.portto.fcl.config.Config.Option.WalletProviders
import com.portto.fcl.config.Network
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.Result
import com.portto.fcl.model.User
import com.portto.fcl.model.authn.AccountProofData
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.Provider
import com.portto.fcl.request.resolve.CadenceResolver.Companion.replaceAddress
import com.portto.fcl.utils.AppUtils
import com.portto.fcl.utils.FclError

object Fcl {
    val config: Config
        get() = Config

    val isMainnet: Boolean
        get() = config.env == Network.MAINNET

    var currentUser: User? = null

    internal var sessionId: String? = null

    fun init(env: Network, supportedWallets: List<Provider>, appDetail: AppDetail? = null): Config =
        config.apply {
            put(Env(env))
            put(WalletProviders(supportedWallets))
            put(App(appDetail))
        }

    /**
     * Retrieve the account address
     */
    suspend fun login(): Result<String> = authenticate()

    /**
     * Retrieve the account address of a user and account proof if data is provided
     * @param accountProofData data to prove the ownership of a Flow account
     * @return Account address
     */
    suspend fun authenticate(accountProofData: AccountProofResolvedData? = null): Result<String> {
        val selectedProvider = config.selectedWalletProvider
            ?: throw FclError.UnspecifiedWalletProviderException()
        return try {
            selectedProvider.authn(accountProofData)
            val address = currentUser?.address
                ?: throw FclError.AccountNotFoundException()
            Result.Success(address)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    /**
     * Allowing the user to personally sign data
     * @param message A raw string to be signed
     * @return A list of [CompositeSignature]
     */
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

    /**
     * Prove a user is in control of a Flow address
     * @param appIdentifier A human-readable string that uniquely identifies your application name
     * @param accountProofData A composite data of nonce, address and signatures
     * @return true if verified; Otherwise false
     */
    suspend fun verifyAccountProof(
        appIdentifier: String,
        accountProofData: AccountProofData
    ): Result<Boolean> = try {
        Result.Success(AppUtils.verifyAccountProof(appIdentifier, accountProofData))
    } catch (e: Exception) {
        Result.Failure(e)
    }

    /**
     * Verify the ownership of a Flow account by verifying a message was signed by a
     * user's private keys.
     * @param message A raw string by which the user was signed
     * @param signatures A list of [CompositeSignature] created from [signUserMessage]
     * @return true if verified; Otherwise false
     */
    suspend fun verifyUserSignatures(
        message: String,
        signatures: List<CompositeSignature>
    ): Result<Boolean> = try {
        Result.Success(AppUtils.verifyUserSignatures(message, signatures))
    } catch (e: Exception) {
        Result.Failure(e)
    }

    /**
     * Remove the current user
     */
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
        Result.Success(AppUtils.query(cadence.replaceAddress(), arguments).value)
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
                cadence = cadence.replaceAddress(),
                args = arguments ?: emptyList(),
                limit = limit,
                authorizers = authorizers
            )
        )
    } catch (e: Exception) {
        Result.Failure(e)
    }

    /**
     * Get current transaction status by specified [transactionId]
     * @param transactionId A hash string represents the transaction
     */
    suspend fun getTransactionStatus(transactionId: String): FlowTransactionResult? =
        AppUtils.getTransactionStatus(transactionId)
}
