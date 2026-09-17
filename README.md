# Avatar Mobile SDK

Thin iOS and Android helper SDKs that present the shared Avatar chat panel in a native WebView.

Customers never pass a panel URL — the host is baked into each release artifact.

| Artifact | Panel host |
|---|---|
| UAT | `https://widget-uat.myegdev2.com` |
| Production | `https://widget.avatar.inc` |

Public API (both platforms): `configure` · `identify` · `present` · `shutdown` · `events`

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

## Android (Maven)

UAT artifact: `com.avatar.inc:widget-uat:0.2.0`  
Production artifact: `com.avatar.inc:widget:0.2.0` (published on production tags only)

Until Maven Central is live, the AAR is on **GitHub Packages**:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/shafiq-jefri/avatar-mobile-sdk")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.avatar.inc:widget-uat:0.2.0")
}
```

GitHub Packages needs a token (`read:packages`) in `~/.gradle/gradle.properties`:

```
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_PAT
```

```kotlin
AvatarWidget.configure(context = this, publishableKey = "pk_live_xxxxxxxx")
AvatarWidget.identify(userId = "usr_123", hmac = token)
AvatarWidget.present(activity = this)
```

Your app’s `applicationId` must be on the avatar’s `mobile_bundles` allowlist.

Dogfood without Maven: clone this repo next to the sample and include `:widget` (see `avatar-sdk-platform/samples/android`).

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

| Tag | SPM | Android Maven |
|---|---|---|
| `0.2.0-uat` | UAT panel | publishes `widget-uat` |
| `0.2.0` | production resolve | publishes `widget` + `widget-uat` |
