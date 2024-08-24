import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    `kotlin-dsl`
}

kotlin {
    explicitApi()
    jvmToolchain(8)

    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        progressiveMode = true
        freeCompilerArgs.add("-Xdont-warn-on-error-suppression")
    }
}

gradlePlugin {
    plugins {
        create("i18n") {
            id = "live.shuuyu.plugins.i18n"
            implementationClass = "live.shuuyu.plugins.i18n.I18nExtensionModule"
        }
    }
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.i18n)
    implementation(libs.kotlin.poet)
}