package com.portto.fcl.utils

import androidx.annotation.WorkerThread
import com.nftco.flow.sdk.bytesToHex
import com.nftco.flow.sdk.hexToBytes
import org.tdf.rlp.RLPCodec

/**
 * rlp-encode account proof data
 *
 * @param includeDomainTag true for encoding sign data; false for encoding verify data
 */
@WorkerThread
fun encodeAccountProof(
    appIdentifier: String,
    address: String,
    nonce: String,
    includeDomainTag: Boolean
): String {
    val encoded = RLPCodec.encode(
        mutableListOf(
            appIdentifier.toByteArray(Charsets.UTF_8),
            address.sansPrefix().padStart(16, '0').hexToBytes(),
            nonce.hexToBytes(),
        )
    )
    val appended = if (includeDomainTag) ACCOUNT_PROOF_DOMAIN_TAG.plus(encoded) else encoded
    return appended.bytesToHex()
}

private val ACCOUNT_PROOF_DOMAIN_TAG = normalize("FCL-ACCOUNT-PROOF-V0.0")

private fun normalize(tag: String): ByteArray {
    val bytes = tag.toByteArray(Charsets.UTF_8)
    return when {
        bytes.size > 32 -> throw IllegalArgumentException("Domain tags cannot be longer than 32 characters")
        bytes.size < 32 -> bytes + ByteArray(32 - bytes.size)
        else -> bytes
    }
}
