plugins {
    java

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.badbones69.crazyenvoys"
version = "1.4.18-${System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"}"
description = "Drop custom crates with any prize you want all over spawn for players to fight over."

repositories {
    mavenCentral()

    maven("https://jitpack.io/")

    maven("https://maven.enginehub.org/repo/")

    maven("https://repo.codemc.org/repository/maven-public/")

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
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

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-[v${rootProject.version}].jar")

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