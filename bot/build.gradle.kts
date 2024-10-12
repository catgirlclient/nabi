import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import live.shuuyu.plugins.i18n.tasks.GenerateI18nFileTask
import live.shuuyu.plugins.i18n.utils.ParserType

plugins {
    id("live.shuuyu.plugins.module")
    id("live.shuuyu.plugins.i18n")
    id("com.gradleup.shadow") version libs.versions.shadow
}

description = "Core packages that allow Nabi to function."

val generate = tasks.register<GenerateI18nFileTask>("generateI18nFileTasks") {
    parserType.set(ParserType.Toml)
    languageInputDirectory.set(file("../resources/locales/en/"))
    languageOutputDirectory.set(file("${project.buildDir}/generated/locale/"))
    generatedPackageName.set("live.shuuyu.nabi.i18n")
}

val shadowTest: Configuration by configurations.creating {
    configurations.testImplementation.get().extendsFrom(this)
}

dependencies {
    implementation(project(":subprojects:cache"))
    implementation(project(":subprojects:database"))
    implementation(project(":subprojects:metrics"))
    implementation(project(":common"))

    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.discord)
    implementation(libs.bundles.database)
    implementation(libs.bundles.jackson)
    implementation(libs.bundles.logger)
    implementation(libs.bundles.caching)
    implementation(libs.bundles.i18n)
    implementation(libs.bundles.ktor)

    shadowTest(libs.bundles.test)
}

sourceSets.main {
    kotlin.srcDirs(generate)
}

tasks {
    withType(ShadowJar::class.java) {
        archiveBaseName.set("Nabi")
        archiveClassifier.set("")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations = listOf(shadowTest)
        exclude("LICENSE.txt")
    }

    processResources {
        from("../resources/locales/en/")
    }

    test {
        useJUnitPlatform()
    }
}