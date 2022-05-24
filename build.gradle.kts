import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    kotlin("jvm")
    id("org.jetbrains.intellij") version "1.6.0"
    id("org.jetbrains.changelog") version "1.1.2"
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

val kotlinVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val platformVersion: String by project
val platformType: String by project

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.15.0")
}

intellij {
    version = platformVersion
    type = platformType
    updateSinceUntilBuild = true

    val plugins = properties("platformPlugins")
        .split(',')
        .map(String::trim)
        .filter(String::isNotEmpty)
    setPlugins(*plugins.toTypedArray())
}

changelog {
    version = project.version.toString()
    groups = emptyList()
    header = closure { version }
    unreleasedTerm = "Latest"
}

detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    withType<Detekt> {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    patchPluginXml {
        version(project.version)
        sinceBuild(pluginSinceBuild)
        untilBuild(pluginUntilBuild)
        pluginDescription(
            closure {
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
            }
        )
        changeNotes(
            closure {
                changelog.getLatest().toHTML()
            }
        )
    }

    runPluginVerifier {
        ideVersions(properties("pluginVerifierIdeVersions"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token(System.getenv("PUBLISH_TOKEN"))
        val releaseChannel = project.version.toString()
            .split('-')
            .getOrElse(1) { "default" }
            .split('.')
            .first()
        channels(releaseChannel)
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
