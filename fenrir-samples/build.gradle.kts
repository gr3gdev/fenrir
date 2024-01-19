plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(project(":fenrir-api"))
    implementation(project(":fenrir-http"))
    implementation(project(":fenrir-jpa"))
    implementation(project(":fenrir-thymeleaf"))

    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    runtimeOnly("com.h2database:h2:2.2.224")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
