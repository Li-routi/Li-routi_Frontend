package com.li_routi.core.data.network.dto.response

data class NotificationReadAllResponse(
    val updatedCount: Int,
)

data class NotificationListResponse(
    val notifications: List<NotificationItemResponse>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)

data class NotificationItemResponse(
    val id: Long,
    val category: String,
    val type: String,
    val title: String,
    val body: String,
    val groupId: Long?,
    val referenceId: Long?,
    val referenceType: String?,
    val read: Boolean,
    val createdAt: String,
)
