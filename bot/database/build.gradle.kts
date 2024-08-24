plugins {
    id("live.shuuyu.plugins.module")
}

kotlin {
    explicitApi() // Forcing explicit api because of java being a nightmare
}

dependencies {
    implementation(libs.bundles.database)
}