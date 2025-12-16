plugins {
    `paper-plugin`
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.fancyinnovations.com/releases/")

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

    compileOnly(libs.bundles.holograms)
    compileOnly(libs.bundles.shared)
    compileOnly(libs.worldguard)
}

tasks {
    shadowJar {
        listOf(
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }

        archiveBaseName.set("${rootProject.name}-${rootProject.version}")

        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    }

    compileJava {
        dependsOn(":api:jar")
    }

    runPaper.folia.registerTask()

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")
        jvmArgs("-Dcom.mojang.eula.agree=true")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}