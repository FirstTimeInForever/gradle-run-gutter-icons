package com.github.firsttimeinforever.gradlerunguttericons.editor

import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction
import org.jetbrains.plugins.gradle.service.project.GradleProjectResolverUtil
import org.jetbrains.plugins.gradle.util.GradleUtil

internal object GradleUtil {
    private fun resolveTaskName(name: String, project: Project, file: VirtualFile): String? {
        val module = ModuleUtil.findModuleForFile(file, project) ?: return null
        val path = GradleProjectResolverUtil.getGradleIdentityPathOrNull(module)?.removeSuffix(":")
        return "${path.orEmpty()}:$name"
    }

    private fun resolveTaskName(name: String, containingFile: PsiFile): String? {
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
