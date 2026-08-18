package com.li_routi.core.domain.routine

/** POST /api/groups/{groupId}/routines/{routineId}/verifications 성공 결과. */
data class GroupRoutineVerification(
    val verificationId: Long,
    val assignmentId: Long,
    val imageKey: String,
    val content: String?,
    val verifiedAt: String?,
)

/** 그룹 루틴 인증 대상 (path의 groupId + routineId). */
data class GroupRoutineTarget(
    val groupId: Long,
    val routineId: Long,
    val verificationId: Long? = null,
)
