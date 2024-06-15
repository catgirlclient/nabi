import live.shuuyu.plugins.project.Project

plugins {
    id("live.shuuyu.plugins.nabi")
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
