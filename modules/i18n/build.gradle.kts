plugins {
    `kotlin-dsl`
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