package com.portto.fcl.request

import com.nftco.flow.sdk.FlowArgument
import com.nftco.flow.sdk.cadence.JsonCadenceBuilder

class TxBuilder {

    internal var cadence: String? = null
        private set

    internal var arguments: List<FlowArgument> = mutableListOf()
        private set

    internal var limit: Int? = null
        private set

    fun cadence(cadence: String) {
        this.cadence = cadence
    }

    fun arguments(arguments: List<FlowArgument>) {
        this.arguments = arguments
    }

    fun arguments(arguments: JsonCadenceBuilder.() -> Iterable<FlowArgument>) {
        val builder = JsonCadenceBuilder()
        this.arguments = arguments(builder).toList()
    }

    fun gasLimit(limit: Int) {
        this.limit = limit
    }
}
