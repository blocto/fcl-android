package com.portto.fcl.model.signable

import com.portto.fcl.utils.sansPrefix
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * transaction
 * {
 *   "f_vsn": "1.0.1",
 *   "f_type": "Signable",
 *   "cadence": " ... ",
 *   "args": [ { arg }, { arg } ],
 *   "message": " ... ",
 *   "keyId": 1,
 *   "addr": "2a96934726085aab",
 *   "roles": { roles },
 *   "interaction": { interaction },
 *   "voucher": { voucher },
 *   "data": { }.
 * }
 */
@Serializable
data class Signable(
    @SerialName("addr")
    val address: String? = null,
    @SerialName("args")
    val args: List<Arg>,
    @SerialName("cadence")
    val cadence: String? = null,
    @SerialName("data")
    val data: Map<String, String>? = mapOf(),
    @SerialName("f_type")
    val fclType: FType = FType.SIGNABLE,
    @SerialName("f_vsn")
    val fclVsn: String = "0.1.0",
    @SerialName("interaction")
    val interaction: Interaction,
    @SerialName("keyId")
    val keyId: Int? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("roles")
    val roles: Roles? = null,
    @SerialName("voucher")
    var voucher: Voucher? = null,
) {
    @Serializable
    enum class FType(val value: String) {
        @SerialName("Signable")
        SIGNABLE("Signable"),

        @SerialName("PreSignable")
        PRE_SIGNABLE("PreSignable")
    }
}

fun Signable.generateVoucher(): Voucher {
    val insideSigners = interaction.findInsideSigners().mapNotNull { id ->
        val account = interaction.accounts[id]
        if (account == null) null else {
            Signature(
                address = account.address?.sansPrefix().orEmpty(),
                keyId = account.keyId,
                sig = account.signature,
            )
        }
    }

    val outsideSigners = interaction.findOutsideSigners().mapNotNull { id ->
        val account = interaction.accounts[id]
        if (account == null) null else {
            Signature(
                address = account.address?.sansPrefix().orEmpty(),
                keyId = account.keyId,
                sig = account.signature,
            )
        }
    }

    return Voucher(
        cadence = interaction.message.cadence,
        refBlock = interaction.message.refBlock,
        computeLimit = interaction.message.computeLimit,
        arguments = interaction.message.arguments.mapNotNull { interaction.arguments[it]?.arg },
        proposalKey = interaction.createProposalKey(),
        payer = interaction.accounts[interaction.payer.orEmpty()]?.address?.sansPrefix(),
        authorizers = interaction.authorizations.mapNotNull {
            interaction.accounts[it]?.address?.sansPrefix()
        }.distinct(),
        payloadSignatures = insideSigners,
        envelopeSignatures = outsideSigners,
    )
}