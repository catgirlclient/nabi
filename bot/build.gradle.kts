import live.shuuyu.plugins.i18n.utils.ParserType

plugins {
    id("live.shuuyu.plugins.module")
    id("live.shuuyu.plugins.i18n")
}

val i18n = tasks.register<GenerateI18nTask>("GenerateI18nTask") {
    parserType.set(ParserType.Toml)
    languageSourceFolder.set(file("../locale/"))
}

dependencies {
    implementation(libs.bundles.discord)
    implementation(libs.bundles.database)
    implementation(libs.bundles.parsers)
    implementation(libs.bundles.logger)
    implementation(libs.bundles.caching)
    implementation(libs.kotlin.protobuf)
    implementation(project(":common"))
    implementation(libs.bundles.ktor)
}

tasks.test {
    useJUnitPlatform()
}