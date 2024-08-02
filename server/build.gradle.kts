plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    idea
}

description = "Server"
logging.captureStandardOutput(LogLevel.INFO)

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findBundle("commonCompile").get())
    implementation(project(":framework"))
    testImplementation(libs.findBundle("testCompile").get())
}