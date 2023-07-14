package com.github.firsttimeinforever.gradlerunguttericons.editor

import com.github.firsttimeinforever.gradlerunguttericons.Messages
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

internal object PluginNotifications {
    private const val groupName = "Gradle Run Gutter Icons"

    private val group: NotificationGroup
        get() = NotificationGroupManager.getInstance().getNotificationGroup(groupName)!!

    fun notifyRunFailed(project: Project, taskName: String) {
        group.createNotification(
            "Could not run task!",
            Messages.message("line.marker.notification.run.failed.text", taskName),
            NotificationType.WARNING
        ).notify(project)
    }
}
