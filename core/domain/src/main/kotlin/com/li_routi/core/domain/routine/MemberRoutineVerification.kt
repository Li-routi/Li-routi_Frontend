package com.li_routi.core.domain.routine

/** POST /api/routines/{routineId}/verifications 성공 결과. */
data class MemberRoutineVerification(
    val verificationId: Long,
    val routineId: Long,
    val imageKey: String,
    val content: String?,
    val verifiedDate: String?,
    val verifiedAt: String?,
)
