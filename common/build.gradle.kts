plugins {
    id("live.shuuyu.plugins.module")
}

kotlin {
    explicitApi() // BECAUSE WE HATE OURSELVES!!!!!!!
}

dependencies {
    implementation(libs.bundles.caching)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.i18n)
}

tasks.test {
    useJUnitPlatform()
}