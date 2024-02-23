plugins {
    `java-library`
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.hibernate.orm:hibernate-core:6.4.2.Final")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0-M1")
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
