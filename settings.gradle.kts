pluginManagement {
    val flutterSdkPath = run {
        val properties = java.util.Properties()
        val androidLocalProperties = file("android/local.properties")
        if (androidLocalProperties.exists()) {
            androidLocalProperties.inputStream().use { properties.load(it) }
        }

        properties.getProperty("flutter.sdk")
            ?: System.getenv("FLUTTER_ROOT")
            ?: throw GradleException(
                "flutter.sdk not set. Create android/local.properties with flutter.sdk=<path> or set FLUTTER_ROOT.",
            )
    }

    includeBuild("$flutterSdkPath/packages/flutter_tools/gradle")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.android.application") version "8.11.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.20" apply false
}

include(":app")
project(":app").projectDir = file("android/app")
