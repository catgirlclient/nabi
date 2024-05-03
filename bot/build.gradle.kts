plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.bundles.discord)
    implementation(libs.bundles.database)
    implementation(libs.bundles.parsers)
    implementation(libs.bundles.logger)
    implementation(project(":common"))
}