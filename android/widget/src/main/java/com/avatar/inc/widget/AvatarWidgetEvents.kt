package com.avatar.inc.widget

import android.os.Handler
import android.os.Looper
import org.json.JSONObject

class AvatarWidgetEvents {
    var onReady: (() -> Unit)? = null
    var onConversationStarted: ((JSONObject) -> Unit)? = null
    var onMessageReceived: ((JSONObject) -> Unit)? = null
    var onUnreadChanged: ((Int) -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    internal fun dispatch(event: String, payload: JSONObject) {
        Handler(Looper.getMainLooper()).post {
            when (event) {
                "WIDGET_READY" -> onReady?.invoke()
                "CONVERSATION_STARTED" -> onConversationStarted?.invoke(payload)
                "MESSAGE_RECEIVED" -> onMessageReceived?.invoke(payload)
                "UNREAD_CHANGED" -> onUnreadChanged?.invoke(payload.optInt("unreadCount", 0))
                "API_STREAM_ERROR" -> onError?.invoke(payload.optString("reason", "Unknown error"))
            }
        }
    }
}
