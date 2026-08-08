package com.li_routi.core.data.network.dto.response

data class GroupRoutineFeedResponse(
    val verifications: List<GroupRoutineVerificationItemResponse> = emptyList(),
    val nextCursor: Long?,
    val hasNext: Boolean = false,
)

data class GroupRoutineVerificationItemResponse(
    val verificationId: Long,
    val assignmentId: Long,
    val memberId: Long,
    val nickname: String,
    val imageUrl: String?,
    val content: String?,
    val verifiedAt: String?,
)
