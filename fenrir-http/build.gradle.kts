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
    api(project(":fenrir-api"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
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

tasks.named<Test>("test") {
    useJUnitPlatform()
}
