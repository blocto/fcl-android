package com.portto.fcl.model.network

import android.net.Uri
import com.portto.fcl.lifecycle.LifecycleObserver
import com.portto.fcl.model.authn.AuthnResponse
import com.portto.fcl.network.ResponseStatus
import com.portto.fcl.webview.WebViewActivity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


typealias BackChannelRpc = Service
typealias Frame = Service

/**
 *  Ref: https://github.com/onflow/fcl-js/blob/master/packages/fcl/src/current-user/normalize/polling-response.js
 */
@Serializable
data class PollingResponse(
    @SerialName("status")
    val status: ResponseStatus,
    @SerialName("reason")
    val reason: String?,
    @SerialName("data")
    val data: AuthnResponse?,
    @SerialName("updates")
    val updates: BackChannelRpc?,
    @SerialName("local")
    val local: Frame?
) {
    fun openAuthenticationWebView() {
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
