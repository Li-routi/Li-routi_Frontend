package com.li_routi.core.data.network.dto.response

/** POST /api/routines/{routineId}/verifications 응답 result. */
data class MemberRoutineVerificationResponse(
    val verificationId: Long,
    val routineId: Long,
    val imageKey: String,
    val content: String?,
    val verifiedDate: String?,
    val verifiedAt: String?,
)
