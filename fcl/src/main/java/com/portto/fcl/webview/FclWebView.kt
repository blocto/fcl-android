package com.portto.fcl.webview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import androidx.viewbinding.BuildConfig

@SuppressLint("SetJavaScriptEnabled")
internal class FclWebView : WebView {
    private var callback: WebViewCallback? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        with(settings) {
            loadsImagesAutomatically = true
            javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            domStorageEnabled = true
        }
        setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
    }

    fun setWebViewCallback(callback: WebViewCallback?) {
        this.callback = callback
    }

    private inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (view.progress == newProgress) {
                callback?.onProgressChange(view.progress / 100f)
            }
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            callback?.onTitleChange(title.orEmpty())
        }
    }

    private inner class WebViewClient : android.webkit.WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, url, isReload)
            callback?.onPageUrlChange(url.orEmpty(), isReload)
        }
    }

    interface WebViewCallback {
        fun onScrollChange(scrollY: Int, offset: Int)
        fun onProgressChange(progress: Float)
        fun onTitleChange(title: String)
        fun onPageUrlChange(url: String, isReload: Boolean)
    }
}