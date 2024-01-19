plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.jackson.databind)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.suite)
    testImplementation("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    filter {
        includeTestsMatching("io.github.gr3gdev.benchmark.TestSuite")
    }
    dependsOn(
        ":benchmark-spring:bootBuildImage",
        ":benchmark-quarkus:imageBuild",
        ":benchmark-fenrir:buildDockerImage"
    )
}
