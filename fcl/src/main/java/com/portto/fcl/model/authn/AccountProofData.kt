package com.portto.fcl.model.authn

import com.portto.fcl.model.CompositeSignature


/**
 * The data is used to verify
 *
 * @property address address of the user authenticating
 * @property nonce minimum 32-byte random nonce as hex string
 * @property signatures [CompositeSignature] signed by the user
 */
data class AccountProofData(
    val nonce: String,
    val address: String,
    val signatures: List<CompositeSignature>
)
