package com.portto.fcl.resolve

import com.portto.fcl.model.signable.Interaction
import com.portto.fcl.utils.AppUtils.flowApi

internal class RefBlockResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        val block = flowApi.getLatestBlock(sealed = true)
        ix.message.refBlock = block.id.base16Value
    }
}
