plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "io.github.gr3gdev"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

gradlePlugin {
    website = "https://github.com/gr3gdev/fenrir/fenrir-gradle-plugin"
    vcsUrl = "https://github.com/gr3gdev/fenrir/fenrir-gradle-plugin"
    val greeting by plugins.creating {
        id = "fenrir.gradle.plugin"
        displayName = "Fenrir gradle-plugin"
        description = "The gradle plugin for use with the Fenrir library"
        tags = listOf("fenrir", "gradle", "plugin", "docker")
        implementationClass = "fenrir.gradle.plugin.FenrirGradlePluginPlugin"
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

gradlePlugin.testSourceSets.add(functionalTestSourceSet)

tasks.named<Task>("check") {
    dependsOn(functionalTest)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
