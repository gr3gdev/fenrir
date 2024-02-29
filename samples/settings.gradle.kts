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

rootProject.name = "samples"
include("common:common-jpa")
include("common:common-thymeleaf")
include("common:common-websocket")
include("sample-basic")
include("sample-login")
include("sample-rest-jpa")
include("sample-thymeleaf")
include("spring:thymeleaf")
