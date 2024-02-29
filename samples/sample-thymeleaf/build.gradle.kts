plugins {
    id("fenrir.gradle.plugin")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.fenrir.thymeleaf)
    implementation(libs.fenrir.jpa)
    implementation(libs.fenrir.file)
    implementation(libs.slf4j.jdk14)
    implementation(project(":common:common-thymeleaf"))
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
    mainClass = "io.github.gr3gdev.sample.ThymeleafApp"
    imageName = "gr3gdev/sample-basic"
}