pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    val fenrirPluginVersion: String by settings
    val fenrirPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
        id(fenrirPluginId) version fenrirPluginVersion
    }
}

rootProject.name = "benchmark"
include("benchmark-spring")
include("benchmark-quarkus")
include("benchmark-fenrir")
include("tests")
include("domain")
