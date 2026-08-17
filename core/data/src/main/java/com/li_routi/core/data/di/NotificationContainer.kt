package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.preference.FcmTokenPreference
import com.li_routi.core.data.repository.NotificationRepositoryImpl
import com.li_routi.core.domain.notification.GetNotificationSettingsUseCase
import com.li_routi.core.domain.notification.GetNotificationsUseCase
import com.li_routi.core.domain.notification.MarkAllNotificationsReadUseCase
import com.li_routi.core.domain.notification.MarkNotificationReadUseCase
import com.li_routi.core.domain.notification.NotificationRepository
import com.li_routi.core.domain.notification.RegisterFcmDeviceUseCase
import com.li_routi.core.domain.notification.UnregisterFcmDeviceUseCase
import com.li_routi.core.domain.notification.UpdateNotificationSettingsUseCase

/**
 * 알림 API 수동 구성 root.
 */
object NotificationContainer {

    val fcmTokenPreference: FcmTokenPreference by lazy {
        FcmTokenPreference(NetworkModule.appContext)
    }

    private val repository: NotificationRepository by lazy {
        NotificationRepositoryImpl(NetworkModule.notificationApiService)
    }

    val registerFcmDeviceUseCase: RegisterFcmDeviceUseCase by lazy {
        RegisterFcmDeviceUseCase(repository)
    }

    val unregisterFcmDeviceUseCase: UnregisterFcmDeviceUseCase by lazy {
        UnregisterFcmDeviceUseCase(repository)
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

    val getNotificationSettingsUseCase: GetNotificationSettingsUseCase by lazy {
        GetNotificationSettingsUseCase(repository)
    }

    val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase by lazy {
        UpdateNotificationSettingsUseCase(repository)
    }
}
