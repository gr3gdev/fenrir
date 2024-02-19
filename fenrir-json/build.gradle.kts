plugins {
    `java-library`
    `maven-publish`
}

group = "io.github.gr3gdev"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    api(project(":fenrir-http"))
    api(libs.jackson.databind)
    api(libs.jackson.jdk8)
    api(libs.jackson.jsr310)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = project.name
            version = version
            from(components["java"])
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}