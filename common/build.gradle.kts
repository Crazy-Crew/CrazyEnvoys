plugins {
    id("root-plugin")
}

dependencies {
    api(libs.configme) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    compileOnly(libs.cluster.api)

    compileOnly("org.jetbrains:annotations:24.1.0")
}