package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState

class ReverifyGroupRoutineUseCase(
    private val repository: RoutineVerificationRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        routineId: Long,
        verificationId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<GroupRoutineVerification> = repository.reverifyGroupRoutine(
        groupId = groupId,
        routineId = routineId,
        verificationId = verificationId,
        mediaKey = mediaKey,
        content = content,
    )
}
