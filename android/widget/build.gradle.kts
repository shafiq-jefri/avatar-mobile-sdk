plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.avatar.inc.widget"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    // Two flavors bake the panel origin into BuildConfig so customers never pass a URL.
    // UAT artifact        → PANEL_ORIGIN = "https://widget-uat.myegdev2.com"
    // Production artifact → PANEL_ORIGIN = "https://widget.avatar.inc"
    flavorDimensions += "avatarEnv"
    productFlavors {
        create("uat") {
            dimension = "avatarEnv"
            buildConfigField(
                "String",
                "PANEL_ORIGIN",
                "\"https://widget-uat.myegdev2.com\"",
            )
        }
        create("production") {
            dimension = "avatarEnv"
            buildConfigField(
                "String",
                "PANEL_ORIGIN",
                "\"https://widget.avatar.inc\"",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }

    publishing {
        singleVariant("uatRelease")
        singleVariant("productionRelease")
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("uatRelease") {
                groupId = "com.avatar.inc"
                artifactId = "widget-uat"
                version = project.findProperty("VERSION_NAME") as String? ?: "0.2.0"
                from(components["uatRelease"])
            }
            create<MavenPublication>("productionRelease") {
                groupId = "com.avatar.inc"
                artifactId = "widget"
                version = project.findProperty("VERSION_NAME") as String? ?: "0.2.0"
                from(components["productionRelease"])
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/shafiq-jefri/avatar-mobile-sdk")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                        ?: (project.findProperty("gpr.user") as String?)
                    password = System.getenv("GITHUB_TOKEN")
                        ?: (project.findProperty("gpr.key") as String?)
                }
            }
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
}
