plugins {
    alias(libs.plugins.runPaper)
    alias(libs.plugins.minotaur)

    alias(libs.plugins.hangar)
    alias(libs.plugins.shadow)

    `maven-publish`
    `java-library`
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")

rootProject.version = if (buildNumber != null) "${libs.versions.minecraft.get()}-$buildNumber" else "1.12.2"

val isBeta = false

val content: String = rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")

    maven("https://repo.papermc.io/repository/maven-public")

    maven("https://repo.codemc.io/repository/maven-public")

    maven("https://repo.crazycrew.us/libraries")
    maven("https://repo.crazycrew.us/releases")

    maven("https://repo.oraxen.com/releases")

    maven("https://maven.enginehub.org/repo")

    maven("https://jitpack.io")

    mavenCentral()
}

dependencies {
    implementation(libs.vital.paper)

    compileOnly(libs.bundles.dependencies)
    compileOnly(libs.bundles.shared)

    compileOnly(libs.paper)
}

val component: SoftwareComponent = components["java"]

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

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

        minecraftVersion(libs.versions.minecraft.get())
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

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")

        listOf(
            "com.ryderbelserion.vital"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to project.group)
        inputs.properties("description" to project.properties["description"])
        inputs.properties("apiVersion" to libs.versions.minecraft.get())
        inputs.properties("authors" to project.properties["authors"])
        inputs.properties("website" to project.properties["website"])

        filesMatching("plugin.yml") {
            expand(inputs.properties)
        }
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))

        projectId.set(rootProject.name.lowercase())

        versionType.set(if (isBeta) "beta" else "release")

        versionName.set("${rootProject.name} ${rootProject.version}")
        versionNumber.set(rootProject.version as String)

        changelog.set(content)

        uploadFile.set(rootProject.projectDir.resolve("jars/${rootProject.name}-${rootProject.version}.jar"))

        gameVersions.set(listOf(libs.versions.minecraft.get()))

        loaders.addAll(listOf("purpur", "paper", "folia"))

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

            channel.set(if (isBeta) "Beta" else "Release")

            changelog.set(content)

            platforms {
                paper {
                    jar.set(rootProject.projectDir.resolve("jars/${rootProject.name}-${rootProject.version}.jar"))

                    platformVersions.set(listOf(libs.versions.minecraft.get()))

                    dependencies {
                        hangar("PlaceholderAPI") {
                            required = false
                        }

                        /*hangar("FancyHolograms") {
                            required = false
                        }**/

                        url(
                            "Oraxen",
                            "https://www.spigotmc.org/resources/%E2%98%84%EF%B8%8F-oraxen-custom-items-blocks-emotes-furniture-resourcepack-and-gui-1-18-1-20-4.72448/"
                        ) {
                            required = false
                        }

                        url(
                            "CMI",
                            "https://www.spigotmc.org/resources/cmi-298-commands-insane-kits-portals-essentials-economy-mysql-sqlite-much-more.3742/"
                        ) {
                            required = false
                        }

                        url(
                            "DecentHolograms",
                            "https://www.spigotmc.org/resources/decentholograms-1-8-1-20-4-papi-support-no-dependencies.96927/"
                        ) {
                            required = false
                        }
                    }
                }
            }
        }
    }
}