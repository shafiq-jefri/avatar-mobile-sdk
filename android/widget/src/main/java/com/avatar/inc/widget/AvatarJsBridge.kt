package com.avatar.inc.widget

import android.webkit.JavascriptInterface
import org.json.JSONObject

internal class AvatarJsBridge(
    private val onEvent: (event: String, payload: JSONObject) -> Unit,
) {
    @JavascriptInterface
    fun postMessage(json: String) {
        val root = JSONObject(json)
        if (root.optString("source") != "avatar_core_iframe") return
        val event = root.optString("event")
        if (event.isEmpty()) return
        val payload = root.optJSONObject("payload") ?: JSONObject()
        onEvent(event, payload)
    }
}
