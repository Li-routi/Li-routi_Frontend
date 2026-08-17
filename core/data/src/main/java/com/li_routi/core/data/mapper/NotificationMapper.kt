package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.request.UpdateNotificationSettingsRequest
import com.li_routi.core.data.network.dto.response.FcmDeviceActiveResponse
import com.li_routi.core.data.network.dto.response.NotificationItemResponse
import com.li_routi.core.data.network.dto.response.NotificationListResponse
import com.li_routi.core.data.network.dto.response.NotificationReadAllResponse
import com.li_routi.core.data.network.dto.response.NotificationSettingsResponse
import com.li_routi.core.domain.notification.AppNotification
import com.li_routi.core.domain.notification.FcmDeviceActive
import com.li_routi.core.domain.notification.NotificationCategory
import com.li_routi.core.domain.notification.NotificationPage
import com.li_routi.core.domain.notification.NotificationReadAllResult
import com.li_routi.core.domain.notification.NotificationSettings
import com.li_routi.core.domain.notification.NotificationSettingsUpdate

fun FcmDeviceActiveResponse.toDomain(): FcmDeviceActive = FcmDeviceActive(active = active)

fun NotificationReadAllResponse.toDomain(): NotificationReadAllResult =
    NotificationReadAllResult(updatedCount = updatedCount)

fun NotificationListResponse.toDomain(): NotificationPage = NotificationPage(
    notifications = notifications.map { it.toDomain() },
    nextCursor = nextCursor,
    hasNext = hasNext,
)

fun NotificationItemResponse.toDomain(): AppNotification = AppNotification(
    id = id,
    category = category.toNotificationCategory(),
    type = type,
    title = title,
    body = body,
    groupId = groupId,
    referenceId = referenceId,
    referenceType = referenceType,
    read = read,
    createdAt = createdAt,
)

fun NotificationCategory.toApiValue(): String? = when (this) {
    NotificationCategory.UNKNOWN -> null
    else -> name
}

private fun String.toNotificationCategory(): NotificationCategory =
    runCatching { NotificationCategory.valueOf(this) }.getOrDefault(NotificationCategory.UNKNOWN)

fun NotificationSettingsResponse.toDomain(): NotificationSettings = NotificationSettings(
    routineDeadlineEnabled = routineDeadlineEnabled,
    newVerificationEnabled = newVerificationEnabled,
    verificationReactionEnabled = verificationReactionEnabled,
    pokeEnabled = pokeEnabled,
    newChatEnabled = newChatEnabled,
    likeEnabled = likeEnabled,
)

fun NotificationSettingsUpdate.toRequest(): UpdateNotificationSettingsRequest = UpdateNotificationSettingsRequest(
    routineDeadlineEnabled = routineDeadlineEnabled,
    newVerificationEnabled = newVerificationEnabled,
    verificationReactionEnabled = verificationReactionEnabled,
    pokeEnabled = pokeEnabled,
    newChatEnabled = newChatEnabled,
    likeEnabled = likeEnabled,
)
