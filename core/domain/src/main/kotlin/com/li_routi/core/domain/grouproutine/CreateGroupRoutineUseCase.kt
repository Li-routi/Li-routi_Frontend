package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class CreateGroupRoutineUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        categoryId: Long,
        title: String,
        description: String,
        schedules: List<GroupRoutineSchedule>,
    ): ResultState<GroupRoutineUpdateResult> = repository.createGroupRoutine(
        groupId = groupId,
        categoryId = categoryId,
        title = title,
        description = description,
        schedules = schedules,
    )
}
