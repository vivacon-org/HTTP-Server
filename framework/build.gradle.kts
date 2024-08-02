plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    idea
}

description = "Framework"
logging.captureStandardOutput(LogLevel.INFO)

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findBundle("commonCompile").get())
    testImplementation(libs.findBundle("testCompile").get())
}