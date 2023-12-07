base {
    archivesName.set("${rootProject.name}-${project.name}")
}

dependencies {
    api(libs.config.me) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    compileOnly(libs.cluster.api)
}