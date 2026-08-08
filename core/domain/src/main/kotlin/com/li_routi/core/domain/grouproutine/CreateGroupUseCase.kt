package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class CreateGroupUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        name: String,
        customCategories: List<NewGroupCategory>,
        routines: List<NewGroupRoutine>,
    ): ResultState<CreatedGroup> = repository.createGroup(name, customCategories, routines)
}
