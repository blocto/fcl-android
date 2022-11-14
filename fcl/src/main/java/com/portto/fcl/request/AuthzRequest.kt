package com.portto.fcl.request

import com.portto.fcl.model.signable.Interaction
import com.portto.fcl.model.signable.toFclArgument
import com.portto.fcl.model.signable.toFlowTransaction
import com.portto.fcl.request.resolve.AccountsResolver
import com.portto.fcl.request.resolve.CadenceResolver
import com.portto.fcl.request.resolve.RefBlockResolver
import com.portto.fcl.request.resolve.SequenceNumberResolver
import com.portto.fcl.request.resolve.SignatureResolver
import com.portto.fcl.utils.AppUtils.flowApi
import com.portto.fcl.utils.NullableAnySerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

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

    @OptIn(ExperimentalSerializationApi::class)
    private fun prepare(builder: TxBuilder): Interaction {
        return Interaction().apply {
            builder.cadence?.let {
                tag = Interaction.Tag.TRANSACTION
                message.cadence = it
            }

            val decodedArgs = builder.arguments.map { arg ->
                Json.decodeFromString(
                    MapSerializer(String.serializer(), NullableAnySerializer),
                    arg.stringValue
                )
            }

            decodedArgs.map { it.toFclArgument() }.apply {
                message.arguments = map { it.tempId }
                arguments = associate { it.tempId to it }
            }

            builder.limit?.let { message.computeLimit = it }
        }
    }
}
