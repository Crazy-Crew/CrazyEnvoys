plugins {
    `java-plugin`
}

dependencies {
    api(project(":api"))

    implementation(libs.hikari.cp)

    compileOnly(libs.bundles.adventure)
    compileOnly(libs.fusion.kyori)
}