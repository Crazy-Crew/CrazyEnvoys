plugins {
    id("root-plugin")

    id("com.github.johnrengelman.shadow")

    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.codemc.org/repository/maven-public/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    shadowJar {
        listOf(
            "de.tr7zw.changeme.nbtapi",
            "dev.triumphteam",
            "org.bstats"
        ).forEach { pack -> relocate(pack, "${rootProject.group}.$pack") }
    }
}