package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class CreateGroupRoutineCategoryUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        name: String,
        color: String?,
    ): ResultState<GroupRoutineCategory> =
        repository.createGroupRoutineCategory(groupId = groupId, name = name, color = color)
}
