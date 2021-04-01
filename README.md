#  ![](src/main/resources/META-INF/pluginIcon_dark.svg) Gradle Run Gutter Icons

![Build](https://github.com/FirstTimeInForever/gradle-run-gutter-icon/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/16443-gradle-run-gutter-icons.svg)](https://plugins.jetbrains.com/plugin/16443-gradle-run-gutter-icons)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/16443-gradle-run-gutter-icons.svg)](https://plugins.jetbrains.com/plugin/16443-gradle-run-gutter-icons)

<!-- Plugin description -->
This simple plugin adds run gutter icons for the `build.gradle.kts` tasks declarations. Then clicked, it executes declared gradle task just like `Execute Gradle Task` action.

Currently, it only supports these types of task declaration:
* `tasks.register(<taskName>)`
* `tasks.create(<taskName>)`
* `val task = tasks.register(<taskName>)`
* `val task = tasks.create(<taskName>)`
* `val task by tasks.registering(<taskName>)`
* `val task by tasks.creating(<taskName>)`

[<img width="400" src="https://raw.githubusercontent.com/FirstTimeInForever/gradle-run-gutter-icons/c260b52a0f3d1117c0b80bfc76cb555fcc113158/images/plugin-screenshot.png" alt="Plugin Screenshot"/>](https://raw.githubusercontent.com/FirstTimeInForever/gradle-run-gutter-icons/c260b52a0f3d1117c0b80bfc76cb555fcc113158/images/plugin-screenshot.png)
<!-- Plugin description end -->

## Installation

* Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Gradle Run Gutter Icons</kbd> >
  <kbd>Install Plugin</kbd>

* Manually:

  Download the [latest release](https://github.com/FirstTimeInForever/gradle-run-gutter-icons/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

Gradle Run Gutter Icons plugin is based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).
