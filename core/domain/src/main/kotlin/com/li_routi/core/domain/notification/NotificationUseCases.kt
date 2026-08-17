package com.li_routi.core.domain.notification

import com.li_routi.core.common.kotlin.util.ResultState

class RegisterFcmDeviceUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(token: String): ResultState<FcmDeviceActive> =
        repository.registerDevice(token)
}

class UnregisterFcmDeviceUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(token: String): ResultState<FcmDeviceActive> =
        repository.unregisterDevice(token)
}

class GetNotificationsUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(
        category: NotificationCategory?,
        cursor: Long? = null,
        size: Int = 20,
    ): ResultState<NotificationPage> = repository.getNotifications(category, cursor, size)
}

private const val UnreadStatusPageSize = 20

// 안 읽은 알림은 보통 최근 것부터 쌓이므로 이 페이지 수까지만 순회한다 — 끝까지 다 순회하면 알림이
// 아주 많은 사용자는 뱃지 하나 켜고 끄는 데 조회량이 과해진다.
private const val UnreadStatusMaxPages = 10

/**
 * 종 아이콘 뱃지용 "안 읽은 알림 있음" 여부. 서버에 별도 개수 API가 없어 목록을 페이지별로 순회하며
 * 안 읽은 알림이 있는지 확인한다. 첫 페이지만 보면, 그 페이지가 전부 읽음 처리돼 있어도 다음 페이지에
 * 안 읽은 알림이 남아 있을 수 있어(그 경우) 뱃지가 잘못 꺼진다.
 */
class HasUnreadNotificationUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(): ResultState<Boolean> {
        var cursor: Long? = null
        repeat(UnreadStatusMaxPages) {
            when (val result = repository.getNotifications(null, cursor, UnreadStatusPageSize)) {
                is ResultState.Success -> {
                    if (result.data.notifications.any { !it.read }) return ResultState.Success(true)
                    if (!result.data.hasNext) return ResultState.Success(false)
                    cursor = result.data.nextCursor
                }
                is ResultState.Error -> return result
                ResultState.Loading -> return ResultState.Loading
            }
        }
        return ResultState.Success(false)
    }
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

class GetNotificationSettingsUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(): ResultState<NotificationSettings> = repository.getSettings()
}

class UpdateNotificationSettingsUseCase(
    private val repository: NotificationRepository,
) {
    suspend operator fun invoke(update: NotificationSettingsUpdate): ResultState<NotificationSettings> =
        repository.updateSettings(update)
}
