plugins {
    id("live.shuuyu.plugins.module")
}

description = "The database in which Nabi's data is stored in."

kotlin {
    explicitApi()
}

dependencies {
    api(project(":common"))

    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.database)
}

tasks.test {
    useJUnitPlatform()
}