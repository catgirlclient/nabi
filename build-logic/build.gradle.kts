plugins {
    kotlin("jvm") version "2.0.0"
    `kotlin-dsl`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
}

dependencies {
    gradleApi()
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(libs.bundles.buildLogic)
}

gradlePlugin {
    plugins {
        register("nabiProjectModule") {
            id = "live.shuuyu.plugins.nabi"
            implementationClass = "live.shuuyu.plugins.NabiProjectModule"
        }
    }
}