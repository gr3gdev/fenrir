plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(project(":fenrir-server"))
    implementation(libs.thymeleaf)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
