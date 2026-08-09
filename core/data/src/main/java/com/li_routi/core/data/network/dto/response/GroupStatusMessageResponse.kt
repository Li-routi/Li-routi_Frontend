package com.li_routi.core.data.network.dto.response

/** PATCH /api/groups/{groupId}/members/me/status-message 응답 result. */
data class GroupStatusMessageResponse(
    val groupId: Long,
    val statusMessage: String,
)
