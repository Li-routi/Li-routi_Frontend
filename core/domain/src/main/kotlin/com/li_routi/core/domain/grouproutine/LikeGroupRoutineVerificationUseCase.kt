package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class LikeGroupRoutineVerificationUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        verificationId: Long,
    ): ResultState<GroupRoutineLike> =
        repository.likeGroupRoutineVerification(groupId = groupId, verificationId = verificationId)
}
