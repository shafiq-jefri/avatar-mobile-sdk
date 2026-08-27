package com.avatar.inc.widget

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.json.JSONObject

class AvatarWidgetActivity : AppCompatActivity() {
    internal companion object {
        @Volatile
        var instance: AvatarWidgetActivity? = null
    }

    private lateinit var webView: WebView
    private var didSendIdentify = false
    private var insetTopPx = 0
    private var insetBottomPx = 0
    private var insetImeBottomPx = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= 29) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        setContentView(R.layout.activity_avatar_widget)

        val toolbar = findViewById<Toolbar>(R.id.avatar_widget_toolbar)
        setSupportActionBar(toolbar)
        toolbar.visibility = View.GONE

        webView = findViewById(R.id.avatar_widget_webview)
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        WebView.setWebContentsDebuggingEnabled(true)

        val root = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            insetTopPx = bars.top
            insetBottomPx = bars.bottom
            insetImeBottomPx = ime.bottom
            applySafeInsets()
            // Handled in CSS (--avatar-ime-bottom). Consuming prevents the
            // WebView from also padding itself and leaving a gap above the keypad.
            WindowInsetsCompat.CONSUMED
        }

        webView.addJavascriptInterface(
            AvatarJsBridge { event, payload ->
                if (event == "WIDGET_READY") sendIdentifyIfNeeded()
                if (event == "WIDGET_CLOSE") runOnUiThread { finish() }
                AvatarWidget.events.dispatch(event, payload)
            },
            "AvatarNative",
        )

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                applySafeInsets()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest,
            ): Boolean {
                val url = request.url
                return if (isPanelHost(url)) {
                    false
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, url))
                    true
                }
            }
        }

        webView.loadUrl(panelUrl())
    }

    private fun applySafeInsets() {
        if (!::webView.isInitialized) return
        val density = resources.displayMetrics.density
        val top = insetTopPx / density
        val bottom = insetBottomPx / density
        val ime = insetImeBottomPx / density
        val keyboardOpen = insetImeBottomPx > 40
        webView.evaluateJavascript(
            "(function(){var r=document.documentElement;" +
                "r.style.setProperty('--avatar-vv-height','100%');" +
                "r.style.setProperty('--avatar-vv-offset','0px');" +
                "r.style.setProperty('--avatar-safe-top','${top}px');" +
                "r.style.setProperty('--avatar-safe-bottom','${bottom}px');" +
                "r.style.setProperty('--avatar-ime-bottom','${ime}px');" +
                "r.classList.toggle('keyboard-open', $keyboardOpen);})()",
            null,
        )
    }

    override fun onDestroy() {
        if (instance === this) instance = null
        webView.removeJavascriptInterface("AvatarNative")
        webView.destroy()
        super.onDestroy()
    }

    internal fun sendIdentifyIfNeeded() {
        val identity = AvatarWidget.identity ?: return
        if (didSendIdentify) return
        postToPanel(
            "WIDGET_IDENTIFY",
            JSONObject()
                .put("userId", identity.first)
                .put("hmac", identity.second),
        )
        didSendIdentify = true
    }

    private fun postToPanel(event: String, payload: JSONObject) {
        payload.put("timestamp", System.currentTimeMillis())
        val envelope = JSONObject()
            .put("source", "avatar_host_script")
            .put("event", event)
            .put("payload", payload)
        val json = envelope.toString()
        webView.post {
            webView.evaluateJavascript(
                "window.postMessage($json, window.location.origin);",
                null,
            )
        }
    }

    private fun panelUrl(): String {
        return Uri.parse(AvatarWidget.panelOrigin + "/").buildUpon()
            .appendQueryParameter("surface", "webview")
            .appendQueryParameter("pk", AvatarWidget.publishableKey)
            .appendQueryParameter("bundle", packageName)
            .build()
            .toString()
    }

    private fun isPanelHost(url: Uri): Boolean {
        val panel = Uri.parse(AvatarWidget.panelOrigin)
        return url.scheme == panel.scheme && url.host == panel.host
    }
}
