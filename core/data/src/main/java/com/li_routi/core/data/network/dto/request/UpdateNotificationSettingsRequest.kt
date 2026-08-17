package com.li_routi.core.data.network.dto.request

/**
 * PATCH `api/notifications/settings` 요청. null 필드는 "변경하지 않음"이라 부분 업데이트가 된다 —
 * 토글 하나만 바꿀 때 그 필드만 채워 보낸다.
 */
data class UpdateNotificationSettingsRequest(
    val routineDeadlineEnabled: Boolean? = null,
    val newVerificationEnabled: Boolean? = null,
    val verificationReactionEnabled: Boolean? = null,
    val pokeEnabled: Boolean? = null,
    val newChatEnabled: Boolean? = null,
    val likeEnabled: Boolean? = null,
)
