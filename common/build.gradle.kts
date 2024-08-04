plugins {
    id("live.shuuyu.plugins.nabi")
}

dependencies {
    implementation(libs.bundles.parsers)
    implementation(libs.bundles.caching)
    implementation(libs.logback)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.i18n)
}

tasks.test {
    useJUnitPlatform()
}