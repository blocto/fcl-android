package com.portto.fcl.model.signable

import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.nftco.flow.sdk.FlowId
import com.nftco.flow.sdk.FlowScript
import com.nftco.flow.sdk.FlowSignature
import com.nftco.flow.sdk.FlowTransaction
import com.nftco.flow.sdk.FlowTransactionProposalKey
import com.nftco.flow.sdk.flowTransaction
import com.portto.fcl.utils.AppUtils.flowApi
import com.portto.fcl.utils.sansPrefix
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class Interaction(
    @SerialName("account")
    var account: Account? = Account(),
    @SerialName("accounts")
    var accounts: Map<String, SignableUser> = mapOf(),
    @SerialName("arguments")
    var arguments: Map<String, Argument> = mutableMapOf(),
    @SerialName("assigns")
    var assigns: Map<String, String> = mapOf(),
    @SerialName("authorizations")
    var authorizations: MutableList<String> = mutableListOf(),
    @SerialName("block")
    var block: Block = Block(),
    @SerialName("collection")
    var collection: Id = Id(),
    @SerialName("events")
    var events: Events = Events(),
    @SerialName("message")
    var message: Message = Message(),
    @SerialName("params")
    var params: Map<String, String> = mapOf(),
    @SerialName("payer")
    var payer: String? = null,
    @SerialName("proposer")
    var proposer: String? = null,
    @SerialName("status")
    var status: InteractionStatus = InteractionStatus.OK,
    @SerialName("tag")
    var tag: Tag = Tag.UNKNOWN,
    @SerialName("transaction")
    var transaction: Id = Id(),
) {
    @Serializable
    enum class InteractionStatus {
        OK, BAD
    }

    @Serializable
    class Account(
        @SerialName("addr")
        var address: String? = null,
    )

    @Serializable
    class Block(
        var id: String? = null,
        var height: Int? = null,
        var isSealed: Boolean? = null,
    )

    @Serializable
    data class Events(
        val blockIds: List<String>? = listOf(),
        var eventType: String? = null,
        var start: String? = null,
        var end: String? = null,
    )

    @Serializable
    data class Id(
        var id: String? = null,
    )

    @Serializable
    data class Message(
        var arguments: List<String> = listOf(),
        var authorizations: List<String> = listOf(),
        var cadence: String? = null,
        var computeLimit: Int? = null,
        var refBlock: String? = null,
        var params: List<String> = listOf(),
    )

    @Serializable
    enum class Tag {
        UNKNOWN,
        SCRIPT,
        TRANSACTION,
    }
}

fun Interaction.toFlowTransaction(): FlowTransaction {
    val ix = this
    val propKey = createFlowProposalKey()

    val payerAccount = payer

    val payer = accounts[payerAccount]?.address ?: throw Exception("missing payer")

    val insideSigners = findInsideSigners()

    val outsideSigners = findOutsideSigners()

    return flowTransaction {
        script(FlowScript(message.cadence.orEmpty()))
        arguments = ix.message.arguments.mapNotNull { ix.arguments[it]?.arg }.map {
            JsonObject(
                mapOf("type" to JsonPrimitive(it.type), "value" to JsonPrimitive(it.value))
            ).toString().toByteArray()
        }.map { FlowArgument(it) }.toMutableList()

        referenceBlockId = FlowId(message.refBlock.orEmpty())
        gasLimit = message.computeLimit ?: 100
        proposalKey = propKey
        payerAddress = FlowAddress(payer)
        authorizers = authorizations.mapNotNull { accounts[it]?.address }
            .distinct().map { FlowAddress(it) }

        addPayloadSignatures {
            insideSigners.forEach { tempID ->
                accounts[tempID]?.let { signableUser ->
                    signature(
                        FlowAddress(signableUser.address.orEmpty()),
                        signableUser.keyId ?: 0,
                        FlowSignature(signableUser.signature.orEmpty())
                    )
                }
            }
        }

        addEnvelopeSignatures {
            outsideSigners.forEach { tempID ->
                accounts[tempID]?.let { signableUser ->
                    signature(
                        FlowAddress(signableUser.address.orEmpty()),
                        signableUser.keyId ?: 0,
                        FlowSignature(signableUser.signature.orEmpty())
                    )
                }
            }
        }
    }
}

fun Interaction.createFlowProposalKey(): FlowTransactionProposalKey {
    val proposer = this.proposer
    val account = accounts[proposer]
    val address = account?.address
    val keyId = account?.keyId

    if (proposer == null || account == null || address == null || keyId == null) {
        throw RuntimeException("Invalid proposer")
    }

    val flowAddress = FlowAddress(address)

    if (account.sequenceNum == null) {
        val flowAccount = flowApi.getAccountAtLatestBlock(flowAddress)
            ?: throw RuntimeException("Get flow account error")
        account.sequenceNum = flowAccount.keys[keyId].sequenceNumber
    }

    return FlowTransactionProposalKey(
        address = FlowAddress(address),
        keyIndex = keyId,
        sequenceNumber = (account.sequenceNum ?: 0).toLong()
    )
}

fun Interaction.createProposalKey(): ProposalKey {
    val proposer = proposer ?: return ProposalKey()
    val account = accounts[proposer] ?: return ProposalKey()
    return ProposalKey(
        address = account.address?.sansPrefix(),
        keyId = account.keyId,
        sequenceNum = account.sequenceNum,
    )
}

fun Interaction.isTransaction() = tag == Interaction.Tag.TRANSACTION

fun Interaction.isScript() = tag == Interaction.Tag.SCRIPT

fun Interaction.buildPreSignable(roles: Roles): Signable {
    return Signable(
        fclType = Signable.FType.PRE_SIGNABLE,
        roles = roles,
        cadence = message.cadence.orEmpty(),
        args = message.arguments.mapNotNull { arguments[it]?.arg },
        interaction = this,
    ).apply {
        voucher = generateVoucher()
    }
}

fun Interaction.findInsideSigners(): List<String> {
    // Inside Signers Are: (authorizers + proposer) - payer
    val inside = authorizations.toMutableSet()
    proposer?.let { inside.add(it) }

    payer?.let { inside.remove(it) }
    return inside.toList()
}

fun Interaction.findOutsideSigners(): List<String> {
    // Outside Signers Are: (payer)
    val payer = payer ?: return emptyList()
    return listOf(payer)
}
