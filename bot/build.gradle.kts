plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.bundles.discord)
    implementation(libs.bundles.database)
    implementation(libs.bundles.parsers)
    implementation(libs.bundles.logger)
    implementation(libs.redisson)
    implementation(project(":common"))
    implementation(libs.bundles.ktor)
    implementation("io.ktor:ktor-client-okhttp-jvm:2.3.11")
}

tasks.test {
    useJUnitPlatform()
}