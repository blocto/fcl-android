package com.portto.fcl.webview

import android.net.Uri
import com.portto.fcl.lifecycle.LifecycleObserver
import com.portto.fcl.model.service.Service
import com.portto.fcl.utils.FclError


fun Service.openAuthenticationWebView() {
    val url = endpoint ?: throw FclError.GeneralException("URL is missing.")

    val context = LifecycleObserver.requireContext()

    val uri = Uri.parse(url).buildUpon().apply {
        params?.forEach { appendQueryParameter(it.key, it.value) }
    }.build()
    WebViewActivity.launchUrl(context, uri.toString())
}

internal object FclWebViewLifecycle {
    private val observers = mutableListOf<WebViewLifecycleObserver>()

    fun addWebViewLifecycleObserver(observer: WebViewLifecycleObserver) {
        observers.add(observer)
    }

    fun removeWebViewLifecycleObserver(observer: WebViewLifecycleObserver) {
        observers.remove(observer)
    }

    fun onWebViewOpen(url: String?) {
        observers.forEach { it.onWebViewOpen(url) }
    }

    fun onWebViewClose(url: String?) {
        observers.forEach { it.onWebViewClose(url) }
    }
}

internal interface WebViewLifecycleObserver {
    fun onWebViewClose(url: String?)

    fun onWebViewOpen(url: String?)
}