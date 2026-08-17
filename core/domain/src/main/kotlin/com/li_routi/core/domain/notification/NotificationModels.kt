package com.li_routi.core.domain.notification

enum class NotificationCategory {
    PERSONAL_ROUTINE,
    CHALLENGE,
    GROUP_ROUTINE,
    CHAT,
    UNKNOWN,
}

data class AppNotification(
    val id: Long,
    val category: NotificationCategory,
    val type: String,
    val title: String,
    val body: String,
    val groupId: Long?,
    val referenceId: Long?,
    val referenceType: String?,
    val read: Boolean,
    val createdAt: String,
)

data class NotificationPage(
    val notifications: List<AppNotification>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)

data class NotificationReadAllResult(
    val updatedCount: Int,
)

data class FcmDeviceActive(
    val active: Boolean,
)

/** GET/PATCH `api/notifications/settings` 조회·변경 결과 — 여섯 알림 유형의 수신 여부. */
data class NotificationSettings(
    val routineDeadlineEnabled: Boolean,
    val newVerificationEnabled: Boolean,
    val verificationReactionEnabled: Boolean,
    val pokeEnabled: Boolean,
    val newChatEnabled: Boolean,
    val likeEnabled: Boolean,
)

/**
 * PATCH `api/notifications/settings` 요청 — null인 필드는 "변경하지 않음"을 뜻하는 부분 업데이트다.
 * 토글 하나를 바꿀 때 그 필드만 채워서 보낸다.
 */
data class NotificationSettingsUpdate(
    val routineDeadlineEnabled: Boolean? = null,
    val newVerificationEnabled: Boolean? = null,
    val verificationReactionEnabled: Boolean? = null,
    val pokeEnabled: Boolean? = null,
    val newChatEnabled: Boolean? = null,
    val likeEnabled: Boolean? = null,
)
