package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class DeleteGroupRoutineUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(groupId: Long, routineId: Long): ResultState<Unit> =
        repository.deleteGroupRoutine(groupId, routineId)
}
