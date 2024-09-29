import live.shuuyu.plugins.i18n.tasks.GenerateI18nFileTask
import live.shuuyu.plugins.i18n.utils.ParserType

plugins {
    id("live.shuuyu.plugins.module")
    id("live.shuuyu.plugins.i18n")
}

val generate = tasks.register<GenerateI18nFileTask>("generateI18nFileTasks") {
    parserType.set(ParserType.Toml)
    languageInputDirectory.set(file("../resources/locales/en/"))
    languageOutputDirectory.set(file("${project.buildDir}/generated/locale/"))
    generatedPackageName.set("live.shuuyu.nabi.i18n")
}

dependencies {
    // TODO: Possibly fatjar this?
    implementation(project(":subprojects:cache"))
    implementation(project(":subprojects:database"))
    implementation(project(":subprojects:metrics"))
    implementation(project(":common"))

    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.discord)
    implementation(libs.bundles.database)
    implementation(libs.bundles.jackson)
    implementation(libs.bundles.parsers)
    implementation(libs.bundles.logger)
    implementation(libs.bundles.caching)
    implementation(libs.kotlin.protobuf)
    implementation(libs.bundles.ktor)
}

sourceSets.main {
    kotlin.srcDirs(generate)
}

tasks {
    processResources {
        from("../resources/locales/en/")
    }

    test {
        useJUnitPlatform()
    }
}