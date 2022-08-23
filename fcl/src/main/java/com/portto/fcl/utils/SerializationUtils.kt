package com.portto.fcl.utils

import kotlinx.serialization.json.*

inline fun <reified T> T.toJsonObject(): JsonObject {
    return Json.encodeToJsonElement(this).jsonObject
}