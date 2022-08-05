plugins {
    java

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

rootProject.group = "com.badbones69.crazyenvoys.CrazyEnvoys"
rootProject.version = "2.4.17-${System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"}"
rootProject.description = "Drop envoys all over the world!"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    // Paper API
    maven("https://repo.papermc.io/repository/maven-public/")

    // Jitpack
    maven("https://jitpack.io")

    // World Guard
    maven("https://maven.enginehub.org/repo/")

    // PAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    // NBT API?
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    // Paper API
    compileOnly(libs.paper)

    implementation(libs.bstats.bukkit)

    implementation(libs.nbt.api)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.decent.holograms)
    compileOnly(libs.holographic.displays)

    compileOnly(libs.world.guard) {
        exclude("com.sk89q", "commandbook")
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }

    compileOnly(libs.world.edit) {
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-[1.8-1.19]-v${rootProject.version}.jar")

        listOf(
            "org.bstats"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
    }

    processResources {
        filesMatching("plugin.yml") {
            expand (
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description
            )
        }
    }

    compileJava {
        options.release.set(17)
    }
}