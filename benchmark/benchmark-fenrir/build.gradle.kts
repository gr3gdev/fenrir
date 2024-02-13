plugins {
    id("fenrir.gradle.plugin")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.fenrir.api)
    implementation(libs.fenrir.http)
    implementation(libs.fenrir.json)
    implementation(libs.fenrir.jpa)
    implementation(libs.slf4j.jdk14)
    implementation(project(":domain"))
    runtimeOnly(libs.postgresql)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testRuntimeOnly(libs.postgresql)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.fenrir.test)
    testImplementation(project(":bench"))
    testImplementation("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

fenrir {
    mainClass = "io.github.gr3gdev.benchmark.fenrir.FenrirApp"
    imageName = "gr3gdev/benchmark-fenrir"
}