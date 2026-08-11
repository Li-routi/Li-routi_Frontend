package com.li_routi.core.data.network.dto.response

data class UnreadGroupRoutineVerificationListResponse(
    val verifications: List<UnreadGroupRoutineVerificationResponse> = emptyList(),
    val nextCursor: Long?,
    val hasNext: Boolean = false,
)

data class UnreadGroupRoutineVerificationResponse(
    val verificationId: Long,
    val authorMemberId: Long,
    val authorName: String,
    val routineName: String,
    val imageUrl: String?,
    val content: String?,
    val verifiedAt: String?,
)
