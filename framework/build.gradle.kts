plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    idea
    jacoco
}

description = "Framework"
logging.captureStandardOutput(LogLevel.INFO)

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findBundle("commonCompile").get())
    testImplementation(libs.findBundle("testCompile").get())
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}