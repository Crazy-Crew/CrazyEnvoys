plugins {
    alias(libs.plugins.minotaur)
    alias(libs.plugins.hangar)

    `java-plugin`
}

val isSnapshot = false

rootProject.version = "1.9.1"

val content: String = rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)

subprojects.filter { it.name != "api" }.forEach {
    it.project.version = rootProject.version
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionType.set(if (isSnapshot) "beta" else "release")

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set(rootProject.version as String)

    changelog.set(content)

    uploadFile.set(rootProject.projectDir.resolve("jars/${rootProject.name}-${rootProject.version}.jar"))

    gameVersions.set(listOf(
        "1.20.6"
    ))

    loaders.add("paper")
    loaders.add("purpur")
    loaders.add("folia")

    autoAddDependsOn.set(false)
    detectLoaders.set(false)

    /*dependencies {
        optional.version("fancyholograms", "2.0.6")
    }**/
}

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_KEY"))

        id.set(rootProject.name.lowercase())

        version.set(rootProject.version as String)

        channel.set(if (isSnapshot) "Snapshot" else "Release")

        changelog.set(content)

        platforms {
            paper {
                jar.set(rootProject.projectDir.resolve("jars/${rootProject.name}-${rootProject.version}.jar"))

                platformVersions.set(listOf(
                    "1.20.6"
                ))

                dependencies {
                    hangar("PlaceholderAPI") {
                        required = false
                    }

                    /*hangar("FancyHolograms") {
                        required = false
                    }**/

                    url("Oraxen", "https://www.spigotmc.org/resources/%E2%98%84%EF%B8%8F-oraxen-custom-items-blocks-emotes-furniture-resourcepack-and-gui-1-18-1-20-4.72448/") {
                        required = false
                    }

                    url("CMI", "https://www.spigotmc.org/resources/cmi-298-commands-insane-kits-portals-essentials-economy-mysql-sqlite-much-more.3742/") {
                        required = false
                    }

                    url("DecentHolograms", "https://www.spigotmc.org/resources/decentholograms-1-8-1-20-4-papi-support-no-dependencies.96927/") {
                        required = false
                    }
                }
            }
        }
    }
}