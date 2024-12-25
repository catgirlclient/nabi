import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    kotlin("jvm")
    id("com.github.node-gradle.node") version "7.1.0"
}

description = "The frontend website for Nabi and all of her correlating services."

repositories {
    mavenCentral() // BECAUSE APPARENTLY IT DOESN'T APPLY FROM DEPENDENCY RESOLUTION MANAGER
}

node {
    version.set("23.5.0")
    pnpmVersion.set("9.15.1")
    download.set(true)
}

val buildTask = tasks.register<PnpmTask>("buildDashboard") {
    args.set(listOf("run", "build"))
    dependsOn(tasks.pnpmInstall)
    inputs.dir(project.fileTree("src"))
    inputs.dir("node_modules")
    inputs.files("uno.config.ts", "astro.config.ts")
    outputs.dir("${project.layout.buildDirectory.get()}/dashboard")
}

sourceSets {
    java {
        main {
            resources {
                srcDir(buildTask)
            }
        }
    }
}
