pluginManagement {
    val fenrirPluginVersion: String by settings
    val fenrirPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(fenrirPluginId) version fenrirPluginVersion
    }
}

rootProject.name = "benchmark"
include("benchmark-spring")
include("benchmark-fenrir")
include("tests")
include("domain")
include("bench")
include("front")
include("websocket")
