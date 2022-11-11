package com.portto.fcl.utils

import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@OptIn(ExperimentalSerializationApi::class)
internal val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
    isLenient = true
    useArrayPolymorphism = true
    coerceInputValues = true
    explicitNulls = false
}

internal inline fun <reified T> T.toJsonObject(): JsonObject =
    json.encodeToJsonElement(this).jsonObject

internal inline fun <reified T> JsonElement.toDataClass() =
    json.decodeFromJsonElement<T>(this)

@ExperimentalSerializationApi
internal object NullableAnySerializer : KSerializer<Any?> {

    override val descriptor: SerialDescriptor
        get() = ContextualSerializer(Any::class, null, emptyArray()).descriptor

    override fun serialize(encoder: Encoder, value: Any?) {
        val json = encoder as? JsonEncoder
            ?: throw IllegalStateException("Only JsonEncoder is supported.")
        json.encodeJsonElement(toJson(value))
    }

    override fun deserialize(decoder: Decoder): Any? {
        val json = decoder as? JsonDecoder
            ?: throw IllegalStateException("Only JsonDecoder is supported.")
        return fromJson(json.decodeJsonElement())
    }

    private fun toJson(item: Any?): JsonElement = when (item) {
        null -> JsonNull
        is String -> JsonPrimitive(item)
        is Number -> JsonPrimitive(item)
        is Boolean -> JsonPrimitive(item)
        is Map<*, *> -> {
            val content = item.map { (k, v) -> k.toString() to toJson(v) }
            JsonObject(content.toMap())
        }
        is List<*> -> {
            val content = item.map { toJson(it) }
            JsonArray(content)
        }
        is JsonElement -> item
        else -> throw IllegalArgumentException("Unable to encode $item")
    }

    private fun fromJson(item: JsonElement): Any? = when (item) {
        JsonNull -> null
        is JsonPrimitive -> when {
            item.isString -> item.content
            item.content == "true" || item.content == "false" -> item.content == "true"
            item.content.contains('.') -> item.content.toDouble()
            else -> item.content.toLong()
        }
        is JsonObject -> item.mapValues { fromJson(it.value) }
        is JsonArray -> item.map { fromJson(it) }
    }
}
