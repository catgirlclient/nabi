plugins {
    id("live.shuuyu.plugins.module")
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.prometheus)
    implementation(libs.bundles.logger)
}