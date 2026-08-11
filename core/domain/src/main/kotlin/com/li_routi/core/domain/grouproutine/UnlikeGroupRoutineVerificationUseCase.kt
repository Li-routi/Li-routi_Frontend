package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class UnlikeGroupRoutineVerificationUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        verificationId: Long,
    ): ResultState<GroupRoutineLike> =
        repository.unlikeGroupRoutineVerification(groupId = groupId, verificationId = verificationId)
}
