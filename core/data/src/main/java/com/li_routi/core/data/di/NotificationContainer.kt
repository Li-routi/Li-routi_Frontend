package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.NotificationRepositoryImpl
import com.li_routi.core.domain.notification.GetNotificationsUseCase
import com.li_routi.core.domain.notification.MarkAllNotificationsReadUseCase
import com.li_routi.core.domain.notification.MarkNotificationReadUseCase
import com.li_routi.core.domain.notification.NotificationRepository

/**
 * 알림 API 수동 구성 root.
 */
object NotificationContainer {

    private val repository: NotificationRepository by lazy {
        NotificationRepositoryImpl(NetworkModule.notificationApiService)
    }

    val getNotificationsUseCase: GetNotificationsUseCase by lazy {
        GetNotificationsUseCase(repository)
    }

    val markNotificationReadUseCase: MarkNotificationReadUseCase by lazy {
        MarkNotificationReadUseCase(repository)
    }

    val markAllNotificationsReadUseCase: MarkAllNotificationsReadUseCase by lazy {
        MarkAllNotificationsReadUseCase(repository)
    }
}
