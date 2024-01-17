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
    implementation(libs.fenrir.rest)
    implementation(libs.fenrir.jpa)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

fenrir {
    mainClass = ""
    imageName = "gr3gdev/benchmark-fenrir"
}