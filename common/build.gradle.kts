plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.bundles.parsers)
    implementation(libs.redisson)
    implementation(libs.bundles.caching)
    implementation(libs.logback)
    implementation(libs.bundles.ktor)
}

tasks.test {
    useJUnitPlatform()
}