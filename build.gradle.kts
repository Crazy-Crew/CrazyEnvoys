import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("root-plugin")

    alias(libs.plugins.modrinth)
    alias(libs.plugins.hangar)
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazyenvoys"
rootProject.description = "Drop custom envoys with any prize you want all over spawn for players to fight over."
rootProject.version = "1.6"

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
 * Added the ability for an item to have damage applied to it.
   * You do need a mod to be able to see how much durability an item has by default in order to apply the correct damage.
```yml
      Items:
        - 'Item:GOLDEN_HELMET, Amount:1, Damage:50, Trim-Pattern:SENTRY, Trim-Material:QUARTZ, Name:&bCheap Helmet, PROTECTION_ENVIRONMENTAL:1, OXYGEN:1'
```
    
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

hangarPublish {
    publications.register("plugin") {
        version.set(rootProject.version as String)
        namespace("CrazyCrew", rootProject.name)
        channel.set("Release")
        changelog.set(description)

        apiKey.set(System.getenv("hangar_key"))

        platforms {
            register(Platforms.PAPER) {
                jar.set(file(combine.get()))
                platformVersions.set(versions)
            }
        }
    }
}