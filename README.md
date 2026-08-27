# Avatar Mobile SDK

Thin iOS and Android helper SDKs that present the shared Avatar chat panel in a native WebView.

Customers never pass a panel URL — the host is baked into each release artifact.

| Artifact | Panel host |
|---|---|
| UAT | `https://widget-uat.myegdev2.com` |
| Production | `https://avatar.inc` |

Public API (both platforms): `configure` · `identify` · `present` · `shutdown` · `events`

---

## iOS (Swift Package Manager)

### Install

In Xcode: **File → Add Package Dependencies…**

```
https://github.com/shafiq-jefri/avatar-mobile-sdk
```

Or in `Package.swift`:

```swift
.package(url: "https://github.com/shafiq-jefri/avatar-mobile-sdk", from: "0.1.0")
```

### UAT vs Production

| Environment | How |
|---|---|
| **UAT** (default) | Resolve the package normally — panel host is UAT |
| **Production** | Resolve with `AVATAR_WIDGET_ENV=production` at package resolve / CI time, or use a production release tag published by Avatar |

### Usage

```swift
import AvatarWidget
import UIKit

// App launch — once
AvatarWidget.configure(publishableKey: "pk_live_xxxxxxxx")

// Open chat
AvatarWidget.present(from: UIApplication.shared.topController!)

// Optional (Beta) — bind authenticated user
AvatarWidget.identify(userId: "usr_123", hmac: token)

// Tear down
AvatarWidget.shutdown()
```

---

## Android (Gradle / Maven)

Until Maven Central publishing is live, include this repo as a composite build or copy the `android/widget` module.

### Flavor

| Flavor | Artifact (planned Maven) | Panel host |
|---|---|---|
| `uat` | `com.avatar.inc:widget-uat` | UAT |
| `production` | `com.avatar.inc:widget` | Production |

### Usage

```kotlin
// Application.onCreate — once
AvatarWidget.configure(context = this, publishableKey = "pk_live_xxxxxxxx")

// Open chat
AvatarWidget.present(activity = this)

// Optional (Beta)
AvatarWidget.identify(userId = "usr_123", hmac = token)

AvatarWidget.shutdown()
```

Your app’s `applicationId` / package name must be on the avatar’s `mobile_bundles` allowlist.

---

## Requirements

| Platform | Minimum |
|---|---|
| iOS | 15.0 · UIKit + WebKit · no third-party deps |
| Android | API 26 · AndroidX AppCompat · Chromium WebView 90+ |

---

## Repo layout

```
Package.swift                 # iOS SPM entry (root)
Sources/AvatarWidget/         # iOS sources
android/                      # Android Gradle project
  widget/                     # library module (uat / production flavors)
```

---

## Versioning

- Tags like `0.1.0` / `1.0.0` — SPM / release markers
- Prefer UAT tags for dogfood; promote the same commit to production after panel + gateway are ready
