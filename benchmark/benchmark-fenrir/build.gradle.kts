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
    implementation(libs.fenrir.jpa)
    implementation(project(":domain"))
    runtimeOnly(libs.postgresql)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

fenrir {
    mainClass = "io.github.gr3gdev.benchmark.fenrir.FenrirApp"
    imageName = "gr3gdev/benchmark-fenrir"
}