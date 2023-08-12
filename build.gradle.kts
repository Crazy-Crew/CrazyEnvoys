plugins {
    `maven-publish`
    `java-library`
}

defaultTasks("build")

rootProject.group = "com.badbones69.crazyenvoys"
rootProject.description = "Drop custom envoys with any prize you want all over spawn for players to fight over."
rootProject.version = "1.6"

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "java-library")

    repositories {
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        maven("https://repo.codemc.org/repository/maven-public/")

        maven("https://repo.papermc.io/repository/maven-public/")

        maven("https://repo.aikar.co/content/groups/aikar/")

        maven("https://repo.triumphteam.dev/snapshots/")

        maven("https://repo.crazycrew.us/snapshots/")

        maven("https://repo.crazycrew.us/releases/")

        maven("https://maven.enginehub.org/repo/")

        maven("https://jitpack.io/")

        mavenCentral()
    }

    listOf(
        ":paper"
    ).forEach {
        project(it) {
            group = "${rootProject.group}.${this.name}"
            version = rootProject.version
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }
    }

    val isSnapshot = rootProject.version.toString().contains("snapshot")

    publishing {
        repositories {
            maven {
                credentials {
                    this.username = System.getenv("gradle_username")
                    this.password = System.getenv("gradle_password")
                }

                if (isSnapshot) {
                    url = uri("https://repo.crazycrew.us/snapshots/")
                    return@maven
                }

                url = uri("https://repo.crazycrew.us/releases/")
            }
        }
    }
}

tasks {
    assemble {
        val jarsDir = File("$rootDir/jars")
        if (jarsDir.exists()) jarsDir.delete()

        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")

            doLast {
                if (!jarsDir.exists()) jarsDir.mkdirs()

                val file = file("${project.buildDir}/libs/${rootProject.name}-${project.version}.jar")

                copy {
                    from(file)
                    into(jarsDir)
                }
            }
        }
    }
}