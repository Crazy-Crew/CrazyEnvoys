rootProject.name = "CrazyEnvoys"

listOf("common", "paper").forEach(::includeProject)

fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name.lowercase()}-$name"
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}