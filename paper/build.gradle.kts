plugins {
    id("io.github.goooler.shadow")

    alias(libs.plugins.run.paper)

    `paper-plugin`
}

dependencies {
    compileOnly(fileTree("$rootDir/libs/compile").include("*.jar"))

    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-ALPHA-10")

    implementation("com.ryderbelserion", "vital-paper", "1.0")

    implementation("ch.jalu", "configme", "1.4.1") {
        exclude("org.yaml")
    }

    implementation(libs.nbt.api)

    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.1.0-SNAPSHOT")

    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.8.6")

    compileOnly("me.clip", "placeholderapi", "2.11.5")

    compileOnly("io.th0rgal", "oraxen", "1.171.0")
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
            "com.ryderbelserion.vital",
            "de.tr7zw.changeme.nbtapi",
            "dev.triumphteam.cmd",
            "ch.jalu"
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

        filesMatching("paper-plugin.yml") {
            expand(properties)
        }
    }
}