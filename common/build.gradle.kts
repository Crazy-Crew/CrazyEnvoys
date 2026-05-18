plugins {
    `java-plugin`
}

dependencies {
    api(project(":api"))

    compileOnly(libs.bundles.adventure)
    compileOnly(libs.fusion.kyori)
}