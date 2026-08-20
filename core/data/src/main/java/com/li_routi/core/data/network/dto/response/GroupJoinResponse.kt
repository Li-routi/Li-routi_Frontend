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
    /** 식별 정보 없이 현재 조합 아바타만 담긴 구성원 미리보기 목록. */
    val members: List<GroupJoinPreviewMemberResponse>?,
    val joinable: Boolean,
    val unavailableReason: String?,
)

data class GroupJoinPreviewMemberResponse(
    val avatar: GroupMemberAvatarResponse?,
)

/** PATCH /api/groups/{groupId}/lock, /unlock 응답 result. */
data class GroupLockStateResponse(
    val groupId: Long,
    val isLocked: Boolean,
)
