package com.li_routi.core.data.network.dto.response

/** PATCH /api/groups/{groupId}/members/me/status-message 응답 result. */
data class GroupStatusMessageResponse(
    val groupId: Long,
    // 상태 메시지를 지우면 서버가 null로 내려줄 수 있어서 nullable로 둠
    val statusMessage: String?,
)
