plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":fenrir-server"))
    implementation(project(":fenrir-thymeleaf"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
