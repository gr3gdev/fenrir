plugins {
    id("fenrir.gradle.plugin")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.fenrir.jpa)
    implementation(libs.fenrir.thymeleaf)
    implementation(libs.fenrir.security)
    implementation(libs.slf4j.jdk14)
    runtimeOnly(libs.h2)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

fenrir {
    mainClass = "io.github.gr3gdev.sample.LoginApp"
    imageName = "gr3gdev/sample-basic"
}