plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(project(":fenrir-api"))
    implementation(project(":fenrir-http"))
    implementation(libs.thymeleaf)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
