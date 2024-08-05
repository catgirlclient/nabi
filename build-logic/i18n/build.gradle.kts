plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("i18n") {
            id = "live.shuuyu.plugins.i18n"
            implementationClass = "live.shuuyu.plugins.i18n.I18nPlugin"
        }
    }
}

kotlin {
    explicitApi()
    compilerOptions {
        progressiveMode.set(true)
        freeCompilerArgs.add("-Xdont-warn-on-error-suppression")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(libs.kotlin.serialization.core)
    implementation(libs.bundles.i18n)

    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
}

tasks.test {
    useJUnitPlatform()
}