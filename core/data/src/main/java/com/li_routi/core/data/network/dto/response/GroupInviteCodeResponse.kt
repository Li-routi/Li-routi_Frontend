package com.li_routi.core.data.network.dto.response

/** POST/GET /api/groups/{groupId}/invite-code 응답 result. */
data class GroupInviteCodeResponse(
    val inviteCode: String,
    val expiresAt: String,
)
