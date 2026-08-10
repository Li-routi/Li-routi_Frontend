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
