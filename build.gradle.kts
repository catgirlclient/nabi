import live.shuuyu.plugins.convention.project.Project

plugins {
    id("live.shuuyu.plugins.module")
}

repositories {
    mavenCentral()
}

tasks.wrapper {
    gradleVersion = "8.10.1"
    distributionType = Wrapper.DistributionType.ALL
}

group = Project.GROUP
version = Project.VERSION
