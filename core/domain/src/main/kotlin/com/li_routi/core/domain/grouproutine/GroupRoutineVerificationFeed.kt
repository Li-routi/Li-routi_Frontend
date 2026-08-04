package com.li_routi.core.domain.grouproutine

data class GroupRoutineVerificationFeed(
    val verifications: List<GroupRoutineVerificationItem>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)

data class GroupRoutineVerificationItem(
    val verificationId: Long,
    val assignmentId: Long,
    val memberId: Long,
    val nickname: String,
    val imageUrl: String?,
    val content: String?,
    val verifiedAt: String?,
)
