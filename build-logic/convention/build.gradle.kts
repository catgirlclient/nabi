plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("nabiProjectModule") {
            id = "live.shuuyu.plugins.module"
            implementationClass = "live.shuuyu.plugins.convention.NabiProjectModule"
        }
    }
}

dependencies {
    gradleApi()
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(libs.bundles.buildLogic)
}