// swift-tools-version:5.9
import PackageDescription

// Panel host is selected at package resolve / CI publish time:
//   unset or anything except "production" → UAT (https://widget-uat.myegdev2.com)
//   AVATAR_WIDGET_ENV=production           → production (https://avatar.inc)
let isProduction = Context.environment["AVATAR_WIDGET_ENV"] == "production"

let package = Package(
    name: "AvatarWidget",
    platforms: [
        .iOS(.v15),
    ],
    products: [
        .library(name: "AvatarWidget", targets: ["AvatarWidget"]),
    ],
    targets: [
        .target(
            name: "AvatarWidget",
            path: "Sources/AvatarWidget",
            swiftSettings: isProduction
                ? [.define("AVATAR_WIDGET_PRODUCTION")]
                : []
        ),
    ]
)
