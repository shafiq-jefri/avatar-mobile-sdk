package com.avatar.inc.widget

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * Thin Android helper SDK — presents the shared chat panel in a WebView.
 * Proposal §5.5 / Appendix C: `configure`, `identify`, `present`, `shutdown`, `events`.
 *
 * Customers always load the platform-hosted panel (non-goal: white-labelled /
 * self-hosted panel origin). The host URL is baked per Maven flavor at build time
 * via BuildConfig.PANEL_ORIGIN — customers never pass a URL.
 */
object AvatarWidget {
    val events: AvatarWidgetEvents = AvatarWidgetEvents()

    /** Platform-hosted panel origin for this SDK artifact (no trailing slash). */
    val panelOrigin: String
        get() = debugPanelOrigin ?: BuildConfig.PANEL_ORIGIN

    /**
     * Dev/sample override — **not part of the customer API**.
     * Set to a local Vite origin (e.g. `"http://10.0.2.2:5173"`) for local dev;
     * `null` restores the baked-in [BuildConfig.PANEL_ORIGIN].
     */
    @Volatile
    var debugPanelOrigin: String? = null

    @Volatile
    internal var publishableKey: String = ""

    @Volatile
    internal var identity: Pair<String, String>? = null

    /** Call once from `Application.onCreate` (proposal Appendix C). */
    @JvmStatic
    fun configure(context: Context, publishableKey: String) {
        this.publishableKey = publishableKey.trim()
    }

    /** Beta — stores the HMAC identity payload and forwards it to the panel when presented. */
    @JvmStatic
    fun identify(userId: String, hmac: String) {
        identity = userId to hmac
        AvatarWidgetActivity.instance?.sendIdentifyIfNeeded()
    }

    @JvmStatic
    fun present(activity: Activity) {
        check(publishableKey.isNotEmpty()) {
            "AvatarWidget.configure(context, publishableKey) must be called first"
        }
        activity.startActivity(Intent(activity, AvatarWidgetActivity::class.java))
    }

    @JvmStatic
    fun shutdown() {
        identity = null
        AvatarWidgetActivity.instance?.finish()
    }
}
