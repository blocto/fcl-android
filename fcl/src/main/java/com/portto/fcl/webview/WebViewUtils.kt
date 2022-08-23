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