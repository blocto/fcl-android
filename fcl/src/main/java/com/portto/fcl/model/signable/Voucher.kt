package com.portto.fcl.model.signable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Voucher(
    @SerialName("arguments")
    val arguments: List<Arg>?,
    @SerialName("authorizers")
    val authorizers: List<String>?,
    @SerialName("cadence")
    val cadence: String?,
    @SerialName("computeLimit")
    val computeLimit: Int?,
    @SerialName("payer")
    val payer: String?,
    @SerialName("payloadSigs")
    val payloadSignatures: List<Signature>?,
    @SerialName("envelopeSigs")
    val envelopeSignatures: List<Signature>?,
    @SerialName("proposalKey")
    val proposalKey: ProposalKey,
    @SerialName("refBlock")
    val refBlock: String?,
)
