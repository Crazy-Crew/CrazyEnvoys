plugins {
    id("root-plugin")

    id("com.modrinth.minotaur") version "2.8.2"
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazyenvoys"
rootProject.description = "Drop custom envoys with any prize you want all over spawn for players to fight over."
rootProject.version = "1.5"

val combine = tasks.register<Jar>("combine") {
    mustRunAfter("build")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val jarFiles = subprojects.flatMap { subproject ->
        files(subproject.layout.buildDirectory.file("libs/${rootProject.name}-${subproject.name}-${subproject.version}.jar").get())
    }.filter { it.name != "MANIFEST.MF" }.map { file ->
        if (file.isDirectory) file else zipTree(file)
    }

    from(jarFiles)
}

tasks {
    assemble {
        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")
        }

        finalizedBy(combine)
    }
}

val description = """
    ## New Features:
    * Armor Trims with all pattern/material support has been added. View how to use it below!
      * https://docs.crazycrew.us/crazyenvoys/prizes/items/armor-trim/
 
    ## Api Changes:
    * `com.badbones69.crazyenvoys:crazyenvoys-api:1.4.20.7` from this point on is outdated.
     * Please update your dependencies to match this version accordingly before updating!
    * `crazyenvoys-api` has been split into `crazyenvoys-core-api` and `crazyenvoys-paper-api` due to future plans for CrazyEnvoys
    * https://docs.crazycrew.us/crazyenvoys/api/intro
    ## Other:
    * [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/discussions/categories/features)
    * [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1"
)

val isSnapshot = rootProject.version.toString().contains("snapshot")
val type = if (isSnapshot) "beta" else "release"

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set("${rootProject.version}")

    uploadFile.set(combine.get())

    gameVersions.addAll(versions)

    changelog.set(description)

    loaders.addAll("paper", "purpur")
}
