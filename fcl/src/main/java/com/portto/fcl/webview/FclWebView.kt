package com.portto.fcl.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Message
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import com.portto.fcl.BuildConfig

@SuppressLint("SetJavaScriptEnabled")
internal class FclWebView : WebView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        settings.setup()
        webChromeClient = WebChromeClient()
        setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
    }

    private inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean = onCreateWebWindow(resultMsg)
    }

    private fun onCreateWebWindow(resultMsg: Message?): Boolean {
        val webView = WebView(context)
        webView.settings.setup()

        webView.webViewClient = object : android.webkit.WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Make WebView height match parent
                val displayRectangle = Rect()
                (context as? Activity)?.window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)
                view?.layoutParams = view?.layoutParams?.apply {
                    height = displayRectangle.height()
                }
            }
        }

        webView.webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onCloseWindow(window: WebView?) {
                super.onCloseWindow(window)
                this@FclWebView.removeView(window)
            }
        }

        this@FclWebView.addView(webView)
        val transport = resultMsg?.obj as WebView.WebViewTransport
        transport.webView = webView
        resultMsg.sendToTarget()
        return true
    }

    private fun WebSettings.setup() {
        javaScriptEnabled = true
        domStorageEnabled = true
        setSupportMultipleWindows(true)
        // Remove "wv" from user agent to make Google login work
        userAgentString = userAgentString.replace(oldValue = "; wv", newValue = "")
    }
}
