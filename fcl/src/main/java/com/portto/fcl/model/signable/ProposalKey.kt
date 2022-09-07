package com.portto.fcl.model.signable

import kotlinx.serialization.Serializable

@Serializable
data class ProposalKey(
    val address: String? = null,
    val keyId: Int? = null,
    val sequenceNum: Int? = null,
)
