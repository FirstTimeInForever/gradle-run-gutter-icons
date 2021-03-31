package com.github.firsttimeinforever.gradlerunguttericons.editor

import com.github.firsttimeinforever.gradlerunguttericons.Messages
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.navigation.GotoRelatedItem
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.util.*
import org.jetbrains.kotlin.idea.core.util.range
import org.jetbrains.kotlin.psi.*

class GradleTasksLineMarkerProvider: RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val dotQualified = (element as? KtDotQualifiedExpression)?.takeIf {
            it.firstChild.text == "tasks"
        } ?: return
        when (dotQualified.parent) {
            is KtPropertyDelegate -> processDelegatedDeclaration(dotQualified, result)
            else -> processPlainDeclaration(dotQualified, result)
        }
    }

    private fun processDelegatedDeclaration(
        dotQualified: KtDotQualifiedExpression,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val callExpression = dotQualified.lastChild as? KtCallExpression ?: return
        val methodElement = callExpression.firstChild as? KtNameReferenceExpression ?: return
        if (methodElement.getReferencedName() !in supportedDelegatesNames) {
            return
        }
        val property = (dotQualified.parent as? KtPropertyDelegate)?.parent as? KtProperty ?: return
        val identifier = property.nameIdentifier ?: return
        val taskName = property.name ?: return
        val marker = createMarker(identifier, taskName, identifier) ?: return
        result.add(marker)
    }

    private fun processPlainDeclaration(
        dotQualified: KtDotQualifiedExpression,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val callExpression = dotQualified.lastChild as? KtCallExpression ?: return
        val methodElement = callExpression.firstChild as? KtNameReferenceExpression ?: return
        if (methodElement.getReferencedName() !in supportedPlainMethodNames) {
            return
        }
        val stringArgument = callExpression.findDescendantOfType<KtStringTemplateExpression>() ?: return
        if (stringArgument.hasInterpolation()) {
            logger.warn("Interpolation is not supported for:\n${DebugUtil.psiToString(methodElement, false)}")
            return
        }
        val taskName = stringArgument.firstChild?.nextSibling?.text ?: return
        val marker = createMarker(methodElement.getReferencedNameElement(), taskName, stringArgument) ?: return
        result.add(marker)
    }

    private fun createMarker(
        element: PsiElement,
        taskName: String,
        gotoElement: PsiElement
    ): RelatedItemLineMarkerInfo<*>? {
        val containingFile = element.containingFile ?: return null
        return RelatedItemLineMarkerInfo(
            element,
            element.range,
            AllIcons.Actions.Execute,
            { Messages.message("line.marker.tooltip.text", taskName) },
            { _, _ -> actuallyRunTask(taskName, containingFile) },
            GutterIconRenderer.Alignment.LEFT,
            { GotoRelatedItem.createItems(mutableListOf(gotoElement)) }
        )
    }

    private fun actuallyRunTask(taskName: String, containingFile: PsiFile) {
        if (!GradleUtil.runTask(taskName, containingFile)) {
            PluginNotifications.notifyRunFailed(containingFile.project, taskName)
        }
    }

    companion object {
        private val logger = logger<GradleTasksLineMarkerProvider>()

        private const val notificationGroup = "firsttimeinforever.gradlerunguttericons"

        private val supportedPlainMethodNames = hashSetOf("register", "create")
        private val supportedDelegatesNames = hashSetOf("registering", "creating")
    }
}
