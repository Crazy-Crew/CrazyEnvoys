plugins {
    `java-library`

    `maven-publish`

    id("com.modrinth.minotaur") version "2.6.0"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    /**
     * NBT API
     */
    maven("https://repo.codemc.org/repository/maven-public/")

    /**
     * Paper Team
     */
    maven("https://repo.papermc.io/repository/maven-public/")

    /**
     * CrazyCrew Team
     */
    maven("https://repo.crazycrew.us/plugins/")

    /**
     * EngineHub Team
     */
    maven("https://maven.enginehub.org/repo/")

    /**
     * Everything else we need.
     */
    maven("https://jitpack.io/")

    mavenCentral()
}

dependencies {
    implementation("de.tr7zw", "nbt-data-api", "2.11.0")

    implementation("org.bstats", "bstats-bukkit", "3.0.0")

    compileOnly("io.papermc.paper", "paper-api", "${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")

    compileOnly("me.filoghost.holographicdisplays", "holographicdisplays-api", "3.0.0")

    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.7.7")

    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.1.0-SNAPSHOT")

    compileOnly("com.Zrips.CMI", "CMI-API", "9.2.6.1")
    compileOnly("net.Zrips.CMILib", "CMI-Lib", "1.2.4.1")

    compileOnly("me.clip", "placeholderapi", "2.11.2") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(project.extra["java_version"].toString()))
}

val isBeta: Boolean = extra["isBeta"].toString().toBoolean()

fun getPluginVersion(): String {
    return if (isBeta) "${project.version}-BETA" else project.version.toString()
}

fun getPluginVersionType(): String {
    return if (isBeta) "beta" else "release"
}

tasks {
    shadowJar {
        archiveFileName.set("${project.name}-${getPluginVersion()}.jar")

        listOf(
            "de.tr7zw",
            "org.bstats"
        ).forEach {
            relocate(it, "${project.group}.plugin.lib.$it")
        }
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(project.name.toLowerCase())

        versionName.set("${project.name} ${getPluginVersion()}")
        versionNumber.set(getPluginVersion())

        versionType.set(getPluginVersionType())

        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(listOf("1.18", "1.18.1", "1.18.2", "1.19", "1.19.1", "1.19.2", "1.19.3"))
        loaders.addAll(listOf("paper", "purpur"))

        //<h3>The first release for CrazyEnvoys on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set("""
                <h2>Changes:</h2>
                 <p>Nice!</p>
                 <p>Added 1.18.2 support.</p>
                <h2>Bug Fixes:</h2>
                 <p>N/A</p>
            """.trimIndent())
    }

    compileJava {
        options.release.set(project.extra["java_version"].toString().toInt())
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to project.name,
                "group" to project.group,
                "version" to getPluginVersion(),
                "description" to project.description,
                "website" to "https://modrinth.com/plugin/${project.name.toLowerCase()}"
            )
        }
    }
}

publishing {
    val mavenExt: String = if (isBeta) "beta" else "releases"

    repositories {
        maven("https://repo.crazycrew.us/$mavenExt") {
            name = "crazycrew"
            //credentials(PasswordCredentials::class)
            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = project.name.toLowerCase()
            version = getPluginVersion()
            from(components["java"])
        }
    }
}