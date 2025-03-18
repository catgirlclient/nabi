package live.shuuyu.plugins.convention

import live.shuuyu.plugins.convention.project.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    signing
    java
}

group = Project.GROUP
version = Project.VERSION

repositories {
    mavenCentral()
    maven("https://repo.kord.dev/snapshots") // Fallback repository
    maven("https://maven.shuyu.me/releases") // Discord InteraKTions repository
    maven("https://oss.sonatype.org/content/repositories/snapshots") // Main snapshot repository
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    configure<KotlinJvmProjectExtension> {
        jvmToolchain(17)

        compilerOptions {
            apiVersion.set(KotlinVersion.KOTLIN_2_2)
            languageVersion.set(KotlinVersion.KOTLIN_2_2)
            progressiveMode = true
            freeCompilerArgs.add("-Xdont-warn-on-error-suppression")
        }
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}