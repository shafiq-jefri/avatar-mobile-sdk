# Avatar Mobile SDK (iOS)

Thin iOS helper SDK that presents the shared Avatar chat panel in a native WebView, distributed via Swift Package Manager.

> The Android SDK is maintained separately in `avatar-sdk-platform/sdk/android` and published to
> `maven.zetrix.com` — see that repo's `docs/ANDROID_SDK_PUBLISHING.md`. It used to also live here
> under `android/`, but that copy was dropped to avoid two sources of truth drifting apart.

Customers never pass a panel URL — the host is baked into each release artifact.

| Artifact | Panel host |
|---|---|
| UAT | `https://widget-uat.myegdev2.com` |
| Production | `https://widget.avatar.inc` |

Public API: `configure` · `identify` · `present` · `shutdown` · `events`

Current release: **`0.2.0-uat`**

---

## iOS (Swift Package Manager)

In Xcode: **File → Add Package Dependencies…**

> **⚠️ Common mistake:** Xcode has two similar-looking windows — **"Add Package Dependency"** (what you want here) and **"Add Package Collection"** (for a curated JSON list of many packages, not a single repo). If you paste this URL and see *"Received invalid response… Please make sure it is a package collection URL"*, you're in the wrong window. Cancel it, and make sure the URL goes into the search field of the **Add Package Dependency** window opened via **File → Add Package Dependencies…** — not a "+" / "Add Package Collection" button inside it.

```
https://github.com/shafiq-jefri/avatar-mobile-sdk
```

Set **Dependency Rule** to **Exact Version**, then pick **`0.2.0-uat`** (UAT panel host).

Or in `Package.swift`:

```swift
.package(url: "https://github.com/shafiq-jefri/avatar-mobile-sdk", exact: "0.2.0-uat")
```

```swift
import AvatarWidget

AvatarWidget.configure(publishableKey: "pk_live_xxxxxxxx")
AvatarWidget.identify(userId: "usr_123", hmac: token) // when identity_verification is enabled
AvatarWidget.present(from: UIApplication.shared.topController!)
```

Default resolve (no `AVATAR_WIDGET_ENV`) uses the UAT panel. Production builds set `AVATAR_WIDGET_ENV=production` at package resolve / CI, or use a production tag.

---

## Requirements

| Platform | Minimum |
|---|---|
| iOS | 15.0 · UIKit + WebKit · no third-party deps |

---

## Repo layout

```
Package.swift                 # iOS SPM entry (root)
Sources/AvatarWidget/         # iOS sources
```

---

## Versioning

| Tag | SPM |
|---|---|
| `0.2.0-uat` | UAT panel |
| `0.2.0` | production resolve |
