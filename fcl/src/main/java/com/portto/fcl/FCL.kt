package com.portto.fcl

import com.portto.fcl.config.Config
import com.portto.fcl.config.Config.Companion.Key.*

object FCL {
    val config = Config()

    fun config(
        appName: String? = null,
        appIconUrl: String? = null,
        accessNode: String? = null,
    ): Config = config.apply {
        appName?.let { put(APP_TITLE, it) }
        appIconUrl?.let { put(APP_ICON, it) }
        accessNode?.let { put(ACCESS_NODE_API, it) }
    }
}