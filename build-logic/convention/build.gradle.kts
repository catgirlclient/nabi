plugins {
    `kotlin-dsl`
}

dependencies {
    gradleApi()
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(libs.bundles.buildLogic)
}