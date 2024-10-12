plugins {
    id("live.shuuyu.plugins.module")
}

description = "The database in which Nabi's data is stored in."

dependencies {
    api(project(":common"))

    implementation(libs.kord.common)
    implementation(libs.kord.core)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.database)
}

tasks.test {
    useJUnitPlatform()
}