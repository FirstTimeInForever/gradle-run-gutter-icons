package com.github.firsttimeinforever.gradlerunguttericon.services

import com.github.firsttimeinforever.gradlerunguttericon.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
