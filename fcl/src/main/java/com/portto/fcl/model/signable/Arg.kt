package com.portto.fcl.model.signable

import com.portto.fcl.utils.NullableAnySerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Arg
 * Ref: https://developers.flow.com/cadence/language/values-and-types
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Arg(
    val type: String,
    @Serializable(with = NullableAnySerializer::class)
    val value: @Contextual Any?
)
