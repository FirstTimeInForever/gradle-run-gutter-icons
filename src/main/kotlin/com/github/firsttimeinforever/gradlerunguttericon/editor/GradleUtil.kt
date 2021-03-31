package com.github.firsttimeinforever.gradlerunguttericon.editor

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction
import org.jetbrains.plugins.gradle.service.project.GradleProjectResolverUtil
import org.jetbrains.plugins.gradle.util.GradleUtil

internal object GradleUtil {
    fun resolveTaskName(name: String, project: Project, virtualFile: VirtualFile): String? {
        val module = ModuleUtil.findModuleForFile(virtualFile, project) ?: return null
        val modulePath = GradleUtil.findGradleModuleData(module)?.let {
            GradleProjectResolverUtil.getGradlePath(it.data).removeSuffix(":")
        } ?: return null
        return "$modulePath:$name"
    }

    fun resolveTaskName(name: String, containingFile: PsiFile): String? {
        return containingFile.virtualFile?.let { file ->
            resolveTaskName(name, containingFile.project, file)
        }
    }

    /**
     * Runs task with *local* name [name] using [GradleExecuteTaskAction.runGradle].
     *
     * @return true if task was scheduled with [GradleExecuteTaskAction.runGradle]
     */
    fun runTask(name: String, containingFile: PsiFile): Boolean {
        val project = containingFile.project
        val projectPath = project.basePath ?: return false
        val task = resolveTaskName(name, containingFile) ?: return false
        GradleExecuteTaskAction.runGradle(project, null, projectPath, task)
        return true
    }
}
