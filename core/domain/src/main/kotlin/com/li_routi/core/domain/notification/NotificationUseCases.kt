package com.li_routi.core.domain.notification

import com.li_routi.core.common.kotlin.util.ResultState

class GetNotificationsUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(
        category: NotificationCategory?,
        cursor: Long? = null,
        size: Int = 20,
    ): ResultState<NotificationPage> = repository.getNotifications(category, cursor, size)
}

class MarkNotificationReadUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(notificationId: Long): ResultState<Unit> =
        repository.markRead(notificationId)
}

class MarkAllNotificationsReadUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(): ResultState<NotificationReadAllResult> =
        repository.markAllRead()
}
