package com.portto.fcl.network

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

    val response = if (data == null) {
        authService.executePost(url, params)
    } else authService.executePost(url, params, data)


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
    PollServiceState.poll()

    val local = response.local()
        ?: throw FclError.GeneralException("No local from polling response.")
    val updates = (response.updates ?: response.authorizationUpdates)
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