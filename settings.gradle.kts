@file:Suppress("UnstableApiUsage")

pluginManagement {
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    }

    includeBuild("build-logic")
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.shuyu.me/releases")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "nabi"

include(
    ":api",
    ":bot",
    ":common",
    ":dashboard"
)