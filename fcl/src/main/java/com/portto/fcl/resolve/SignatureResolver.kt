package com.portto.fcl.resolve

import com.nftco.flow.sdk.DomainTag
import com.nftco.flow.sdk.bytesToHex
import com.portto.fcl.model.AuthData
import com.portto.fcl.model.signable.*
import com.portto.fcl.utils.toDataClass
import com.portto.fcl.utils.toJsonObject

class SignatureResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {

        if (ix.tag != Interaction.Tag.TRANSACTION) throw Error("Interaction tag error")

        val insideSigners = ix.findInsideSigners()

        val tx = ix.toFlowTransaction()

        ix.accounts[ix.proposer.orEmpty()]?.sequenceNum = tx.proposalKey.sequenceNumber.toInt()

        val insidePayload = (DomainTag.TRANSACTION_DOMAIN_TAG + tx.canonicalPayload).bytesToHex()

        val publishers = insideSigners.map { fetchSignature(ix, insidePayload, it) }

        publishers.forEach {
            val id = it.first
            val signature = it.second
            ix.accounts[id]?.signature = signature
        }

        val outsideSigners = ix.findOutsideSigners()
        val outsidePayload =
            (DomainTag.TRANSACTION_DOMAIN_TAG + ix.toFlowTransaction().canonicalAuthorizationEnvelope).bytesToHex()

        val outPublishers = outsideSigners.map { fetchSignature(ix, outsidePayload, it) }

        outPublishers.forEach {
            val id = it.first
            val signature = it.second
            ix.accounts[id]?.signature = signature
        }
    }

    private suspend fun fetchSignature(
        ix: Interaction,
        payload: String,
        id: String
    ): Pair<String, String> {
        val acct = ix.accounts[id] ?: throw RuntimeException("Can't find account by id")
        val signingFunction = acct.signingFunction ?: throw RuntimeException()
        val signable = buildSignable(ix, payload, acct)

        val response = signingFunction.invoke(signable.toJsonObject())

        val authData = response.data?.toDataClass<AuthData>()

        return Pair(id, authData?.signature ?: response.compositeSignature?.signature.orEmpty())
    }

    private fun buildSignable(ix: Interaction, payload: String, account: SignableUser): Signable {
        return Signable(
            message = payload,
            keyId = account.keyId,
            address = account.address,
            roles = account.role,
            cadence = ix.message.cadence,
            args = ix.message.arguments.mapNotNull { ix.arguments[it]?.arg },
            interaction = ix,
        ).apply {
            voucher = generateVoucher()
        }
    }
}