plugins {
    id("java")
}

group = "org.vivacon"
version = "0.0.1"

repositories {
    mavenCentral()
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    testImplementation(libs.findBundle("testCompile").get())
}

tasks.test {
    useJUnitPlatform()
}