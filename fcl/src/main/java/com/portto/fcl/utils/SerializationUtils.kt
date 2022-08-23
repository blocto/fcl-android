package com.portto.fcl.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
    isLenient = true
    useArrayPolymorphism = true
    coerceInputValues = true
    explicitNulls = false
}

internal inline fun <reified T> T.toJsonObject(): JsonObject {
    return Json.encodeToJsonElement(this).jsonObject
}

internal inline fun <reified T> JsonElement.toDataClass() =
    json.decodeFromJsonElement<T>(this)