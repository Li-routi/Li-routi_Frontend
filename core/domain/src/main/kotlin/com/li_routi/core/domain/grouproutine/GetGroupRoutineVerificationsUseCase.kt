package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class GetGroupRoutineVerificationsUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        routineId: Long,
        cursor: Long? = null,
        size: Int? = null,
    ): ResultState<GroupRoutineVerificationFeed> =
        repository.getGroupRoutineVerifications(
            groupId = groupId,
            routineId = routineId,
            cursor = cursor,
            size = size,
        )
}
