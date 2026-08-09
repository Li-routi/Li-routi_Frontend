package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class UpdateGroupNameUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(groupId: Long, name: String): ResultState<Unit> =
        repository.updateGroupName(groupId, name)
}
