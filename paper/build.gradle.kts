import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.userdev)
    alias(libs.plugins.modrinth)
    alias(libs.plugins.hangar)
}

repositories {
    flatDir { dirs("libs") }
}

dependencies {
    compileOnly("cmi-api:CMI-API")
    compileOnly("cmi-lib:CMILib")

    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.1.0-SNAPSHOT")

    implementation("de.tr7zw", "item-nbt-api", "2.11.3")
    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")

    compileOnly(fileTree("libs").include("*.jar"))

    compileOnly("me.clip", "placeholderapi", "2.11.3")

    compileOnly("com.github.decentsoftware-eu", "decentholograms","2.8.3")

    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(component)
            }
        }
    }

    reobfJar {
        outputJar.set(file("$buildDir/libs/${rootProject.name}-${project.version}.jar"))
    }

    assemble {
        dependsOn(reobfJar)
    }

    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")

        listOf(
            "dev.triumphteam",
            "org.bstats",
            "de.tr7zw"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val props = mapOf(
            "name" to rootProject.name,
            "group" to project.group,
            "version" to rootProject.version,
            "description" to rootProject.description,
            "authors" to rootProject.properties["authors"],
            "apiVersion" to "1.20",
            "website" to "https://modrinth.com/plugin/${rootProject.name.lowercase()}"
        )

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val description = """
## New Features:
 * Added the ability for an item to have damage applied to it.
   * You do need a mod to be able to see how much durability an item has by default in order to apply the correct damage.
```yml
Items:
  - 'Item:GOLDEN_HELMET, Amount:1, Damage:50, Trim-Pattern:SENTRY, Trim-Material:QUARTZ, Name:&bCheap Helmet'
```
    
## Other:
* [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/discussions/categories/features)
* [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1"
    //"1.20.2"
)

val output = file("${rootProject.rootDir}/jars/${rootProject.name}-${project.version}.jar")

val isSnapshot = rootProject.version.toString().contains("snapshot")
val type = if (isSnapshot) "beta" else "release"

modrinth {
    autoAddDependsOn.set(false)

    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set("${rootProject.version}")

    uploadFile.set(output)

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
                jar.set(output)
                platformVersions.set(versions)
            }
        }
    }
}