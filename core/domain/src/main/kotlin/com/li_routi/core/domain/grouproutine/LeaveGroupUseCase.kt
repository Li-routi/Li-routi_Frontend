package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class LeaveGroupUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(groupId: Long): ResultState<LeaveGroupResult> =
        repository.leaveGroup(groupId)
}
