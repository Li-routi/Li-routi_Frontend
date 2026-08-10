package com.li_routi.core.data.network.dto.request

/** PATCH /api/groups/{groupId}/members/me/status-message 요청 body. */
data class UpdateStatusMessageRequest(
    val statusMessage: String,
)

/** PATCH /api/groups/{groupId}/name 요청 body. */
data class UpdateGroupNameRequest(
    val name: String,
)

/** PATCH /api/groups/{groupId}/owner 요청 body. */
data class TransferGroupOwnerRequest(
    val targetMemberId: Long,
)
