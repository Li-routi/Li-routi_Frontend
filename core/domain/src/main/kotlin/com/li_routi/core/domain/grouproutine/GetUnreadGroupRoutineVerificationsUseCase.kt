package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class GetUnreadGroupRoutineVerificationsUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        cursor: Long? = null,
        size: Int? = null,
    ): ResultState<UnreadGroupRoutineVerificationFeed> =
        repository.getUnreadGroupRoutineVerifications(
            groupId = groupId,
            cursor = cursor,
            size = size,
        )
}
