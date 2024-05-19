plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.bundles.parsers)
    implementation(libs.slf4j)
}