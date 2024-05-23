import com.ryderbelserion.feather.enums.Repository
import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("java-plugin")
}

dependencies {
    compileOnly(libs.paper)
}

feather {
    repository("https://repo.extendedclip.com/content/repositories/placeholderapi")

    repository("https://repo.triumphteam.dev/snapshots")

    repository("https://maven.enginehub.org/repo")

    repository(Repository.Paper.url)
}