plugins {
    id("fenrir.gradle.plugin")
}

repositories {
    mavenCentral()
}

dependencies {
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