package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState

interface RoutineVerificationRepository {

    /** 개인 루틴을 mediaKey로 인증한다. */
    suspend fun verifyMemberRoutine(
        routineId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<MemberRoutineVerification>

    /** 오늘 배정된 그룹 루틴을 mediaKey로 인증한다. */
    suspend fun verifyGroupRoutine(
        groupId: Long,
        routineId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<GroupRoutineVerification>

    suspend fun reverifyGroupRoutine(
        groupId: Long,
        routineId: Long,
        verificationId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<GroupRoutineVerification>
}
