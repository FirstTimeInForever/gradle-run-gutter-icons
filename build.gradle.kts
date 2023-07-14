import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    kotlin("jvm")
    id("org.jetbrains.intellij") version "1.15.0"
    id("org.jetbrains.changelog") version "1.3.1"
}

val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val platformVersion: String by project
val platformType: String by project

repositories {
    mavenCentral()
}

intellij {
    version.set(platformVersion)
    type.set(platformType)
    updateSinceUntilBuild.set(true)

    val plugins = properties("platformPlugins")
        .split(',')
        .map(String::trim)
        .filter(String::isNotEmpty)
    this.plugins.set(plugins)
}

changelog {
    version.set(project.version.toString())
    groups.set(emptyList())
    header.set(version)
    unreleasedTerm.set("Unreleased")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        languageVersion.set(KotlinVersion.KOTLIN_1_7)
        apiVersion.set(KotlinVersion.KOTLIN_1_7)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    patchPluginXml {
        version.set(project.version.toString())
        sinceBuild.set(pluginSinceBuild)
        untilBuild.set(pluginUntilBuild)
        pluginDescription.set(provider {
            val readmeLines = File(projectDir, "README.md").readText().lines()
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"
            if (!readmeLines.containsAll(listOf(start, end))) {
                throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
            }
            val html = readmeLines.run {
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n")
            markdownToHTML(html)
        })
        changeNotes.set(provider {
            changelog.getLatest().toHTML()
        })
    }

    runPluginVerifier {
        ideVersions.set(provider {
            val versions = properties("pluginVerifierIdeVersions")
            versions.split(',').map(String::trim).filter(String::isNotEmpty)
        })
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        val releaseChannel = project.version.toString()
            .split('-')
            .getOrElse(1) { "default" }
            .split('.')
            .first()
        channels.set(listOf(releaseChannel))
    }
}

tasks.withType<RunIdeTask> {
    jvmArgs(
        "--add-exports",
        "java.base/jdk.internal.vm=ALL-UNNAMED",
        "-Xmx4096m",
        "-Xms128m"
    )
}
