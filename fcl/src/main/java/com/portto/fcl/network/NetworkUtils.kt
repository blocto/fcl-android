package com.portto.fcl.network

import android.net.Uri
import android.util.Log
import com.portto.fcl.lifecycle.LifecycleObserver
import com.portto.fcl.model.PollingResponse
import com.portto.fcl.model.service.Service
import com.portto.fcl.webview.WebViewActivity

internal object NetworkUtils {
    suspend fun repeatWhen(predicate: suspend () -> Boolean, block: suspend () -> Unit) {
        while (predicate()) {
            block.invoke()
        }
    }

    suspend fun polling(service: Service): PollingResponse {
        Log.d("NetworkUtils", "Test - service: $service")
        val url = service.endpoint ?: throw Error("No endpoint")
        Log.d("NetworkUtils", "Test - url: $url")
        val response = FclClient.authService.executeGet(url, service.params)
        Log.d("NetworkUtils", "Test - response: $response")
        when (response.status) {
            ResponseStatus.APPROVED,
            ResponseStatus.DECLINED -> WebViewActivity.close()
            else -> return response
        }
        return response
    }

    fun PollingResponse.openAuthenticationWebView() {
        local ?: throw Error()
        val url = local.endpoint ?: throw Error()
        val params = local.params ?: throw Error()

        val uri = Uri.parse(url).buildUpon().apply {
            params.forEach { appendQueryParameter(it.key, it.value) }
        }.build()
        LifecycleObserver.context()?.apply {
            WebViewActivity.launchUrl(this, uri.toString())
        }
    }
}


