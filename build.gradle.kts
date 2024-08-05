import live.shuuyu.plugins.convention.project.Project

plugins {
    id("live.shuuyu.plugins.module")
}

repositories {
    mavenCentral()
}

tasks.wrapper {
    gradleVersion = "8.8"
    distributionType = Wrapper.DistributionType.ALL
}

group = Project.GROUP
version = Project.VERSION
