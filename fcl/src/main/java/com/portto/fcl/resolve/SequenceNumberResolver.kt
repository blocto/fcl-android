package com.portto.fcl.resolve

import com.nftco.flow.sdk.FlowAddress
import com.portto.fcl.model.signable.Interaction
import com.portto.fcl.utils.AppUtils.flowApi

internal class SequenceNumberResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        val proposer = ix.proposer
        val account = ix.accounts[proposer]
        val address = account?.address
        val keyId = account?.keyId

        if (proposer == null || account == null || address == null || keyId == null) {
            throw RuntimeException("Some necessary data is null")
        }

        val flowAddress = FlowAddress(address)

        if (account.sequenceNum != null) {
            return
        }

        val flowAccount = flowApi.getAccountAtLatestBlock(flowAddress)
            ?: throw RuntimeException("Get flow account error")

        ix.accounts[proposer]?.sequenceNum = flowAccount.keys[keyId].sequenceNumber
    }
}
