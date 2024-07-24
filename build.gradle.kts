plugins {
    `java-library`
    `java-gradle-plugin`
    `kotlin-dsl`
    idea
}

group = "org.vivacon"
version = "0.0.1"
description = "simple_http_server"
java.sourceCompatibility = JavaVersion.VERSION_1_8

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

