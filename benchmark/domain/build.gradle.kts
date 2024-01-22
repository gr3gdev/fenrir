plugins {
    id("java")
}

repositories {
    mavenCentral()
    mavenLocal()
}

group = "io.github.gr3gdev"

dependencies {
    implementation("org.hibernate.orm:hibernate-core:6.4.2.Final")
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
