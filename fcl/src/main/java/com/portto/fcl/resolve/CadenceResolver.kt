package com.portto.fcl.resolve

import com.portto.fcl.model.signable.Interaction
import com.portto.fcl.model.signable.isScript
import com.portto.fcl.model.signable.isTransaction

internal class CadenceResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        if (!(ix.isTransaction() || ix.isScript())) {
            return
        }
        var cadence = ix.message.cadence ?: return

        // todo: Add address replacement
//        Fcl.config.data().filter { it.key.startsWith("0x") }.forEach { cadence = cadence.replace(it.key, it.value) }

        ix.message.cadence = cadence
    }
}
