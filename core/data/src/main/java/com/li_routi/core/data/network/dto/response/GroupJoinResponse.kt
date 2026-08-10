package com.li_routi.core.data.network.dto.response

/** POST /api/groups/join 응답 result. */
data class GroupJoinResultResponse(
    val groupId: Long,
    val name: String,
    val memberStatus: String,
)

/** GET /api/groups/join/preview 응답 result. */
data class GroupJoinPreviewResponse(
    val groupId: Long,
    val name: String,
    val activeMemberCount: Int,
    val maxMemberCount: Int,
    val totalRoutineCount: Int,
    val joinable: Boolean,
    val unavailableReason: String?,
)

/** PATCH /api/groups/{groupId}/lock, /unlock 응답 result. */
data class GroupLockStateResponse(
    val groupId: Long,
    val isLocked: Boolean,
)
