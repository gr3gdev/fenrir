import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("java")
    id("org.springframework.boot") version ("3.2.1")
    id("io.spring.dependency-management") version ("1.1.4")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(project(":domain"))
    runtimeOnly("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

configurations.implementation {
    exclude("org.springframework.boot:spring-boot-starter-tomcat")
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("gr3gdev/benchmark-spring")
}
