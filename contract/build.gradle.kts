plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    idea
}

val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findBundle("commonCompile").get())
}