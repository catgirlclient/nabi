plugins {
    id("live.shuuyu.plugins.module")
}

description = "Caches all sequestered information in Nabi to improve performance!"

dependencies {
    api(project(":common"))
    implementation(libs.kord.common)
    implementation(libs.kord.core)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.caching)
}