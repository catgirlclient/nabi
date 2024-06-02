plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.bundles.parsers)
    implementation(libs.bundles.caching)
    implementation(libs.slf4j)
}