plugins {
    id("io.github.goooler.shadow")

    alias(libs.plugins.run.paper)

    `paper-plugin`
}

dependencies {
    compileOnly(fileTree("$rootDir/libs/compile").include("*.jar"))

    //implementation(libs.metrics)

    implementation(libs.triumph.cmds)

    implementation(libs.config.me)

    implementation(libs.nbt.api)

    implementation(libs.vital)

    compileOnly(libs.decent.holograms)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.itemsadder.api)

    compileOnly(libs.oraxen.api)

    compileOnly(libs.worldguard)
}

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        repositories {
            maven {
                url = uri("https://repo.crazycrew.us/releases")

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

        minecraftVersion("1.20.5")
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
            "com.ryderbelserion.vital",
            "de.tr7zw.changeme.nbtapi",
            "dev.triumphteam.cmd",
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val properties = hashMapOf(
            "name" to rootProject.name,
            "version" to project.version,
            "group" to project.group,
            "description" to rootProject.description,
            "apiVersion" to "1.20",
            "authors" to rootProject.properties["authors"],
            "website" to rootProject.properties["website"]
        )

        inputs.properties(properties)

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}