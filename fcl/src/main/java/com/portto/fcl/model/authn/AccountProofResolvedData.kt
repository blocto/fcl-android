package com.portto.fcl.model.authn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The resolved data should include a specific application identifier (appIdentifier), a
 * random nonce and config contains Blocto dApp id.
 * This data will be sent to the wallet for signing by the user.
 * If the user approves and authentication is successful, a signature is returned to the client
 *
 * @property appIdentifier human-readable string that uniquely identifies your application name
 * @property nonce minimum 32-byte random nonce as hex string
 * @property config Blocto dApp id
 */
@Serializable
data class AccountProofResolvedData(
    @SerialName("appIdentifier")
    val appIdentifier: String? = null,
    @SerialName("nonce")
    val nonce: String? = null,
    @SerialName("config")
    val config: Config,
)

@Serializable
data class Config(
    @SerialName("app")
    val app: AppConfig,
)

@Serializable
data class AppConfig(
    @SerialName("id")
    val id: String,
)
