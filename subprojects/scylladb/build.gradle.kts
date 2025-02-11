plugins {
    id("live.shuuyu.plugins.module")
}

dependencies {
    implementation(libs.bundles.discord)
    implementation(libs.bundles.scylladb)
}