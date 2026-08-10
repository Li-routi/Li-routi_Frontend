package com.li_routi.core.domain.grouproutine

data class UnreadGroupRoutineVerificationFeed(
    val verifications: List<UnreadGroupRoutineVerification>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)

data class UnreadGroupRoutineVerification(
    val verificationId: Long,
    val authorMemberId: Long,
    val authorName: String,
    val routineName: String,
    val imageUrl: String?,
    val content: String?,
    val verifiedAt: String?,
)
