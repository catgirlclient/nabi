plugins {
    id("live.shuuyu.plugins.module")
}

description = "The module which tracks statistics in Nabi's services."

dependencies {
    api(project(":common"))

    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.prometheus)
    implementation(libs.bundles.logger)
}

tasks.test {
    useJUnitPlatform()
}