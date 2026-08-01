package com.li_routi.core.data.network.dto.response

/** POST /api/groups/{groupId}/routines/{routineId}/verifications 응답 result. */
data class GroupRoutineVerificationResponse(
    val verificationId: Long,
    val assignmentId: Long,
    val imageKey: String,
    val content: String?,
    val verifiedAt: String?,
)
