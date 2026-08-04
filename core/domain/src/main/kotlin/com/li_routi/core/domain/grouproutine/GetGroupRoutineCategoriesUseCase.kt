package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class GetGroupRoutineCategoriesUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(groupId: Long): ResultState<GroupRoutineCategoryList> =
        repository.getGroupRoutineCategories(groupId)
}
