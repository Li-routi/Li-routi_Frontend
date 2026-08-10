package com.li_routi.core.data.network.dto.response

/** GET /api/groups/{groupId}/invite-code 응답 result. 서버가 expiresAt을 빼면서 코드만 내려줌 */
data class GroupInviteCodeResponse(
    val inviteCode: String,
)
