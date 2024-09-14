plugins {
    id("live.shuuyu.plugins.module")
}

dependencies {
    implementation(libs.kord.common)
    implementation(libs.kord.core)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.database)
    implementation(libs.bundles.database.future)
}