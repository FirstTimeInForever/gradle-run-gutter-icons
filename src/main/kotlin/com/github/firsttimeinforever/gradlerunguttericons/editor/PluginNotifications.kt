package com.github.firsttimeinforever.gradlerunguttericons.editor

import com.github.firsttimeinforever.gradlerunguttericons.Messages
import com.intellij.notification.NotificationBuilder
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

internal object PluginNotifications {
    val NOTIFICATION_GROUP by lazy {
        NotificationGroupManager.getInstance().getNotificationGroup("Gradle Run Gutter Icons")!!
    }

    fun notifyRunFailed(project: Project, taskName: String) {
        NotificationBuilder(
            NOTIFICATION_GROUP.displayId,
            Messages.message("line.marker.notification.run.failed.text", taskName),
            NotificationType.WARNING
        ).apply {
            setTitle("Could not run task!")
            buildAndNotify(project)
        }
    }
}
