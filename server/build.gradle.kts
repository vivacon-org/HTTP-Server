plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    idea
}

description = "Server"
logging.captureStandardOutput(LogLevel.INFO)

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findBundle("serverCompile").get())
    runtimeOnly(libs.findBundle("serverRuntime").get())
    implementation(project(":framework"))
}