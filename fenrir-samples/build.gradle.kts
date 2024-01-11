plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":fenrir-api"))
    implementation(project(":fenrir-http"))
    implementation(project(":fenrir-thymeleaf"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
