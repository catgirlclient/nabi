package live.shuuyu.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class NabiProjectModule: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
            val kotlinExtension = extensions.getByType<KotlinJvmProjectExtension>()

            group = live.shuuyu.plugins.project.Project.GROUP
            version = live.shuuyu.plugins.project.Project.VERSION

            repositories {
                google()
                mavenLocal()
                mavenCentral()
                gradlePluginPortal()
                maven("https://maven.shuyu.me/releases")
                maven("https://oss.sonatype.org/content/repositories/snapshots")
            }

            kotlinExtension.apply {
                jvmToolchain(17) // Minimum (LTS) Java version we support

                compilerOptions {
                    progressiveMode.set(true)
                    freeCompilerArgs.add("-Xdont-warn-on-error-suppression")
                }

                sourceSets.all {
                    languageSettings {
                        optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                        optIn("kotlin.experimental.ExperimentalTypeInference")
                    }
                }
            }

            tasks.apply {
                withType<Test>().configureEach {
                    useJUnitPlatform()
                }
            }
        }
    }
}