package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class DisappointGroupRoutineVerificationUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        verificationId: Long,
    ): ResultState<GroupRoutineDisappointment> =
        repository.disappointGroupRoutineVerification(groupId = groupId, verificationId = verificationId)
}
