plugins {
    id("live.shuuyu.plugins.nabi")
}

dependencies {
    implementation(project(":common"))
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.logger)
    implementation(libs.bundles.parsers)
    implementation(libs.bundles.ktor)
}