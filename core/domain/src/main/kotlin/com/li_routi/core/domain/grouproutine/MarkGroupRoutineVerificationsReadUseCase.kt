package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class MarkGroupRoutineVerificationsReadUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        lastReadVerificationId: Long,
    ): ResultState<GroupRoutineVerificationRead> =
        repository.markGroupRoutineVerificationsRead(
            groupId = groupId,
            lastReadVerificationId = lastReadVerificationId,
        )
}
