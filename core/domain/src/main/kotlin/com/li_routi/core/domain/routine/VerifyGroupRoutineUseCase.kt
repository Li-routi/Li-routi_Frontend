package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState

class VerifyGroupRoutineUseCase(
    private val repository: RoutineVerificationRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        routineId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<GroupRoutineVerification> = repository.verifyGroupRoutine(
        groupId = groupId,
        routineId = routineId,
        mediaKey = mediaKey,
        content = content,
    )
}
