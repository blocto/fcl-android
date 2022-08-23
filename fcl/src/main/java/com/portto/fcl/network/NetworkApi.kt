/*
 * MIT License
 * Copyright (c) 2021 Zed
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.portto.fcl.network

import android.util.Log
import androidx.annotation.WorkerThread
import com.portto.fcl.model.PollingResponse
import com.portto.fcl.model.service.Service
import com.portto.fcl.network.FclClient.authService
import com.portto.fcl.utils.CoroutineUtils.repeatWhen
import com.portto.fcl.utils.CoroutineUtils.runBlockDelay
import com.portto.fcl.utils.FclError
import com.portto.fcl.webview.FclWebViewLifecycle
import com.portto.fcl.webview.WebViewActivity
import com.portto.fcl.webview.WebViewLifecycleObserver
import com.portto.fcl.webview.openAuthenticationWebView
import kotlinx.serialization.json.JsonObject

@WorkerThread
internal suspend fun execHttpPost(
    url: String,
    params: Map<String, String>? = mapOf(),
    data: JsonObject? = null
): PollingResponse {
    Log.d("execHttpPost", "Test - url: $url")
    Log.d("execHttpPost", "Test - params: $params")
    Log.d("execHttpPost", "Test - data: $data")
    val response = if (data == null) {
        authService.executePost(url, params)
    } else authService.executePost(url, params, data)
    Log.d("execHttpPost", "Test - response: $response")
    Log.d("execHttpPost", "Test - status: ${response.status}")

    when (response.status) {
        ResponseStatus.APPROVED -> WebViewActivity.close()
        ResponseStatus.DECLINED -> {
            WebViewActivity.close()
            throw FclError.UserDeclinedException()
        }
        ResponseStatus.PENDING -> return tryPollService(response)
    }

    return response
}

private suspend fun tryPollService(
    response: PollingResponse,
): PollingResponse {
    Log.d("tryPollService", "Test - response: $response")

    PollServiceState.poll()

    val local = response.local
        ?: throw FclError.GeneralException("No local from polling response.")
    val updates = response.updates
        ?: throw FclError.GeneralException("No updates from polling response.")

    try {
        local.openAuthenticationWebView()
    } catch (e: Exception) {
        throw FclError.GeneralException(e.message.toString())
    }

    var pollResponse: PollingResponse? = null

    repeatWhen(predicate = { pollResponse == null || pollResponse?.status == ResponseStatus.PENDING }) {
        runBlockDelay(1000) {
            pollResponse = poll(updates)
        }
    }

    Log.d("tryPollService", "Test - pollResponse: $pollResponse")
    Log.d("tryPollService", "Test - response: $response")
    return pollResponse ?: response
}

private suspend fun poll(service: Service): PollingResponse? {
    if (!PollServiceState.isPollEnable()) {
        return null
    }

    val url = service.endpoint ?: throw FclError.GeneralException("URL not found.")

    val response = authService.executeGet(url, service.params)

    when (response.status) {
        ResponseStatus.APPROVED -> WebViewActivity.close()
        ResponseStatus.DECLINED -> {
            WebViewActivity.close()
            throw FclError.UserDeclinedException()
        }
        else -> return response
    }
    return response
}

private object PollServiceState {
    private var canContinue = false

    init {
        FclWebViewLifecycle.addWebViewLifecycleObserver(object : WebViewLifecycleObserver {
            override fun onWebViewClose(url: String?) {
                canContinue = false
            }

            override fun onWebViewOpen(url: String?) {
            }
        })
    }

    fun poll() {
        canContinue = true
    }

    fun stopPoll() {
        canContinue = false
    }

    fun isPollEnable() = canContinue
}