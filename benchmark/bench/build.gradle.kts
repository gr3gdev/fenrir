plugins {
    `java-library`
}

repositories {
    mavenCentral()
    mavenLocal()
}

group = "io.github.gr3gdev"

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
