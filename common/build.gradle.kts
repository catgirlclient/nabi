plugins {
    id("live.shuuyu.plugins.nabi")
}

dependencies {
    implementation(libs.bundles.parsers)
    implementation(libs.redisson)
    implementation(libs.bundles.caching)
    implementation(libs.logback)
    implementation(libs.bundles.ktor)
}

tasks.test {
    useJUnitPlatform()
}