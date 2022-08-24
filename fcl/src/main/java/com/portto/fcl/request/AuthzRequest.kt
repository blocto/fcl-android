package com.portto.fcl.request

import android.util.Log
import com.portto.fcl.model.signable.Interaction
import com.portto.fcl.model.signable.toFclArgument
import com.portto.fcl.model.signable.toFlowTransaction
import com.portto.fcl.resolve.AccountsResolver
import com.portto.fcl.resolve.CadenceResolver
import com.portto.fcl.resolve.RefBlockResolver
import com.portto.fcl.resolve.SequenceNumberResolver
import com.portto.fcl.utils.AppUtils.flowApi
import com.portto.fcl.resolve.SignatureResolver


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
        Log.d("AuthzRequest", "Test - send interaction: $interaction")
        Log.d("AuthzRequest", "Test - send flow tx: ${interaction.toFlowTransaction()}")
        val id = flowApi.sendTransaction(interaction.toFlowTransaction())
        Log.d("AuthzRequest", "Test - send id: $id")
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