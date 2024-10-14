plugins {
    id("live.shuuyu.plugins.module")
}

dependencies {
    implementation(project(":common"))
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.logger)
    implementation(libs.bundles.ktor)
}