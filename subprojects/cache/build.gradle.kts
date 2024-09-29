plugins {
    id("live.shuuyu.plugins.module")
}

dependencies {
    api(project(":common"))
    implementation(libs.kord.common)
    implementation(libs.kord.core)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.caching)
}