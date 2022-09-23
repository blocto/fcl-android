package com.portto.fcl.request.resolve

import com.portto.fcl.Fcl
import com.portto.fcl.model.signable.Interaction
import com.portto.fcl.model.signable.isScript
import com.portto.fcl.model.signable.isTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class CadenceResolver : Resolver {

    override suspend fun resolve(ix: Interaction) {
        if (!(ix.isTransaction() || ix.isScript())) {
            return
        }
        val cadence = ix.message.cadence?.replaceAddress() ?: return

        ix.message.cadence = cadence
    }

    companion object {
        suspend fun String.replaceAddress(): String = withContext(Dispatchers.Default) {
            var cadence = this@replaceAddress
            Fcl.config.addressReplacement.forEach { replacement ->
                if (!replacement.address.startsWith("0x")) {
                    throw Exception("Invalid address format: ${replacement.address}")
                }
                cadence = cadence.replace(replacement.placeholder, replacement.address)
            }
            return@withContext cadence
        }
    }
}
