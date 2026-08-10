package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class DeleteGroupUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(groupId: Long): ResultState<Unit> =
        repository.deleteGroup(groupId)
}
