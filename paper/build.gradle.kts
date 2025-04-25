plugins {
    id("paper-plugin")

    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
}

project.group = "${rootProject.group}"
project.version = rootProject.version
project.description = "Drop custom envoys with any prize you want all over spawn for players to fight over."

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.fancyinnovations.com/releases/")

    maven("https://repo.oraxen.com/releases/")

    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    implementation(libs.vital.paper)

    implementation(libs.metrics)

    compileOnly(libs.bundles.dependencies)
    compileOnly(libs.bundles.shared)

    compileOnly(libs.paper)
}

tasks {
    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")

        listOf(
            "com.ryderbelserion.vital",
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    assemble {
        dependsOn(shadowJar)

        doLast {
            copy {
                from(shadowJar.get())
                into(rootProject.projectDir.resolve("jars"))
            }
        }
    }

    processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to project.group)
        inputs.properties("apiVersion" to libs.versions.minecraft.get())
        inputs.properties("description" to project.description)
        inputs.properties("authors" to rootProject.properties["authors"].toString())
        inputs.properties("website" to rootProject.properties["website"].toString())

        filesMatching("plugin.yml") {
            expand(inputs.properties)
        }
    }

    runPaper.folia.registerTask()

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}