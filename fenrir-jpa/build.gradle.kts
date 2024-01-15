plugins {
    `java-library`
    `maven-publish`
}

group = "io.github.gr3gdev"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.hibernate)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = project.name
            version = version
            from(components["java"])
        }
    }
}
