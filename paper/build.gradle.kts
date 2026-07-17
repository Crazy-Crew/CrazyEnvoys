plugins {
    `paper-plugin`
}

project.group = "${rootProject.group}"

repositories {
    maven("https://repo.fancyinnovations.com/releases/")

    maven("https://repo.momirealms.net/releases/")

    maven("https://repo.hibiscusmc.com/releases/")

    maven("https://maven.enginehub.org/repo/")

    maven("https://repo.nexomc.com/releases/")

    maven("https://repo.oraxen.com/releases/")

    maven("https://maven.devs.beer/")
}

dependencies {
    implementation(project(":api"))

    implementation(libs.fusion.paper)

    implementation(libs.triumph.cmds)

    implementation(libs.metrics)

    implementation(libs.jalu)

    compileOnly(libs.bundles.holograms)
    compileOnly(libs.bundles.shared)
    compileOnly(libs.bundles.cmi)
    compileOnly(libs.worldguard)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        listOf(
            "com.ryderbelserion.fusion",
            "io.leangen.geantyref",
            "dev.triumphteam.cmd",
            "org.spongepowered",
            "com.google.gson",
            "org.jspecify",
            "org.bstats",
            "org.yaml",
            "ch.jalu"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    runPaper.folia.registerTask()

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")
        jvmArgs("-Dcom.mojang.eula.agree=true")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}