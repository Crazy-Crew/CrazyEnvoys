plugins {

    id("java-plugin")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://maven.enginehub.org/repo/")

    maven("https://repo.oraxen.com/releases/")
}


dependencies {
    compileOnly(libs.paper)
}