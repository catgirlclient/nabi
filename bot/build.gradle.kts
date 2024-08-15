plugins {
    id("live.shuuyu.plugins.module")
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.discord)
    implementation(libs.bundles.database)
    implementation(libs.bundles.parsers)
    implementation(libs.bundles.logger)
    implementation(libs.bundles.caching)
    implementation(libs.kotlin.protobuf)
    implementation(project(":common"))
    implementation(libs.bundles.ktor)
}

tasks.test {
    useJUnitPlatform()
}