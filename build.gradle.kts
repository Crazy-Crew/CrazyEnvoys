plugins {
    java

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")

val jenkinsVersion = "1.4.18-b$buildNumber"

group = "com.badbones69.crazyenvoys"
version = "1.4.18"
description = "Drop custom crates with any prize you want all over spawn for players to fight over."

repositories {

    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    /**
     * Spigot Team
     */
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    /**
     * NBT API
     */
    maven("https://repo.codemc.org/repository/maven-public/")

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
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")

    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")

    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.7.2")

    compileOnly("me.clip:placeholderapi:2.11.2") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }

    implementation("de.tr7zw:nbt-data-api:2.10.0")

    implementation("org.bstats:bstats-bukkit:3.0.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    shadowJar {
        if (buildNumber != null) {
            archiveFileName.set("${rootProject.name}-[v${jenkinsVersion}].jar")
        } else {
            archiveFileName.set("${rootProject.name}-[v${rootProject.version}].jar")
        }

        listOf(
            "de.tr7zw",
            "org.bstats"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
    }

    compileJava {
        options.release.set(17)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description
            )
        }
    }
}