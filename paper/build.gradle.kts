plugins {
    id("io.github.goooler.shadow")

    alias(libs.plugins.run.paper)

    `paper-plugin`
}

dependencies {
    implementation(libs.triumph.cmds)

    implementation(libs.vital.paper)

    compileOnly(fileTree("$projectDir/libs/compile").include("*.jar"))

    compileOnly(fileTree("$rootDir/libs/compile").include("*.jar"))

    compileOnly(libs.decent.holograms)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.oraxen.api)

    //compileOnly(libs.worldguard)
}

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        repositories {
            maven {
                url = uri("https://repo.crazycrew.us/releases/")

                credentials {
                    this.username = System.getenv("gradle_username")
                    this.password = System.getenv("gradle_password")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(component)
            }
        }
    }

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion("1.20.6")
    }

    assemble {
        doLast {
            copy {
                from(shadowJar.get())
                into(rootProject.projectDir.resolve("jars"))
            }
        }
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")

        listOf(
            "com.ryderbelserion",
            "dev.triumphteam",
            "ch.jalu"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to project.group)
        inputs.properties("description" to project.properties["description"])
        inputs.properties("website" to project.properties["website"])

        filesMatching("paper-plugin.yml") {
            expand(inputs.properties)
        }
    }
}