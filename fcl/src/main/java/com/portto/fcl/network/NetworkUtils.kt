package com.portto.fcl.network

import com.portto.fcl.model.network.PollingResponse
import com.portto.fcl.model.network.Service
import com.portto.fcl.webview.WebViewActivity

internal object NetworkUtils {
    suspend fun repeatWhen(predicate: suspend () -> Boolean, block: suspend () -> Unit) {
        while (predicate()) {
            block.invoke()
        }
    }

    suspend fun polling(service: Service): PollingResponse {
        val url = service.endpoint ?: throw Error()
        val response = FclClient.authService.executeGet(url, service.params)
        when (response.status) {
            ResponseStatus.APPROVED,
            ResponseStatus.DECLINED -> WebViewActivity.close()
            else -> return response
        }
        return response
    }
}


