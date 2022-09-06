package com.portto.fcl.model.signable

import com.nftco.flow.sdk.cadence.Field
import kotlinx.serialization.Serializable

/**
 * @see [Interaction]
 */
@Serializable
data class Argument(
    val arg: Arg,
    val kind: String,
    val tempId: String,
    val value: String,
    val xform: Xform
) {
    @Serializable
    data class Xform(
        val label: String
    )
}

fun <T> Field<T>.toFclArgument(): Argument {
    return Argument(
        arg = Arg(type, value.toString()),
        kind = "ARGUMENT",
        value = value.toString(),
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
