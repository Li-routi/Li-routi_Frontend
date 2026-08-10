package com.li_routi.core.domain.notification

import com.li_routi.core.common.kotlin.util.ResultState

interface NotificationRepository {
    suspend fun getNotifications(
        category: NotificationCategory?,
        cursor: Long?,
        size: Int = 20,
    ): ResultState<NotificationPage>
    suspend fun markRead(notificationId: Long): ResultState<Unit>
    suspend fun markAllRead(): ResultState<NotificationReadAllResult>
}
