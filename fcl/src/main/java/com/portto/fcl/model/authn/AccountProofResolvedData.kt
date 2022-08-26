package com.portto.fcl.model.authn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The resolved data should include a specific application identifier (appIdentifier) and a
 * random nonce. This data will be sent to the wallet for signing by the user.
 * If the user approves and authentication is successful, a signature is returned to the client
 *
 * @property appIdentifier human-readable string that uniquely identifies your application name
 * @property nonce minimum 32-byte random nonce as hex string
 */
@Serializable
data class AccountProofResolvedData(
    @SerialName("accountProofIdentifier")
    val appIdentifier: String,
    @SerialName("accountProofNonce")
    val nonce: String,
)
