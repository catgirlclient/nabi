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
        maven("https://maven.shuyu.me/releases")
        maven("https://maven.shuyu.me/snapshots")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.shuyu.me/releases")
        maven("https://maven.shuyu.me/snapshots")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "nabi"

include(
    // ":api",
    ":bot",
    ":bot:gateway",
    ":common",
    ":dashboard",
    ":subprojects:cache",
    ":subprojects:database",
    ":subprojects:entities",
    ":subprojects:metrics",
    ":subprojects:scylladb"
)