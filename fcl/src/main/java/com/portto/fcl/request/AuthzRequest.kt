package com.portto.fcl.request

import com.portto.fcl.model.signable.Interaction
import com.portto.fcl.model.signable.toFclArgument
import com.portto.fcl.model.signable.toFlowTransaction
import com.portto.fcl.resolve.*
import com.portto.fcl.utils.AppUtils.flowApi


internal class AuthzRequest {

    suspend fun send(builder: TxBuilder.() -> Unit): String {
        val interaction = prepare(TxBuilder().apply { builder(this) })
        listOf(
            CadenceResolver(),
            AccountsResolver(),
            RefBlockResolver(),
            SequenceNumberResolver(),
            SignatureResolver(),
        ).forEach { it.resolve(interaction) }

        val id = flowApi.sendTransaction(interaction.toFlowTransaction())

        return id.base16Value
    }

    private fun prepare(builder: TxBuilder): Interaction {
        return Interaction().apply {
            builder.cadence?.let {
                tag = Interaction.Tag.TRANSACTION
                message.cadence = it
            }
            builder.arguments.map { it.toFclArgument() }.apply {
                message.arguments = map { it.tempId }
                arguments = associate { it.tempId to it }
            }

            builder.limit?.let { message.computeLimit = it }
        }
    }
}