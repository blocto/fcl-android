package com.portto.fcl.network

import com.portto.fcl.model.network.PollingResponse
import com.portto.fcl.model.network.Service
import com.portto.fcl.webview.WebViewActivity

internal object NetworkUtils {
    const val RESPONSE_APPROVED = "APPROVED"
    const val RESPONSE_DECLINED = "DECLINED"
    const val RESPONSE_PENDING = "PENDING"


    suspend fun repeatWhen(predicate: suspend () -> Boolean, block: suspend () -> Unit) {
        while (predicate()) {
            block.invoke()
        }
    }

    suspend fun polling(service: Service): PollingResponse {
        val url = service.endpoint ?: throw Error()
        val response = FclClient.authService.executeGet(url, service.params)
        when (response.status) {
            RESPONSE_APPROVED -> WebViewActivity.close()
            RESPONSE_DECLINED -> WebViewActivity.close()
            else -> return response
        }
        return response
    }
}


