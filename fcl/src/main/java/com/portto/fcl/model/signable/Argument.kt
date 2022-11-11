package com.portto.fcl.model.signable

import com.portto.fcl.utils.NullableAnySerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

/**
 * @see [Interaction]
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Argument(
    val arg: Arg,
    val kind: String,
    val tempId: String,
    @Serializable(with = NullableAnySerializer::class)
    val value: @Contextual Any?,
    val xform: Xform
) {
    @Serializable
    data class Xform(
        val label: String
    )
}

fun Map<String, Any?>.toFclArgument(): Argument {
    val type: String = get("type") as String
    val value = get("value")
    return Argument(
        arg = Arg(type, value),
        kind = "ARGUMENT",
        value = value,
        xform = Argument.Xform(type),
        tempId = randomId(10),
    )
}

/**
 * Get random alphanumeric ID with the given [length]
 */
private fun randomId(length: Int = 10): String = (1..length)
    .map { (('a'..'z') + ('0'..'9')).random() }
    .joinToString("")
