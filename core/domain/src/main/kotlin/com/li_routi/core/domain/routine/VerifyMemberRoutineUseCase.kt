package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState

class VerifyMemberRoutineUseCase(
    private val repository: RoutineVerificationRepository,
) {
    suspend operator fun invoke(
        routineId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<MemberRoutineVerification> = repository.verifyMemberRoutine(
        routineId = routineId,
        mediaKey = mediaKey,
        content = content,
    )
}
