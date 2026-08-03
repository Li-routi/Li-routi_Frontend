package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class UpdateGroupRoutineUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        routineId: Long,
        categoryId: Long,
        title: String,
        description: String,
        schedules: List<GroupRoutineSchedule>,
    ): ResultState<GroupRoutineUpdateResult> = repository.updateGroupRoutine(
        groupId = groupId,
        routineId = routineId,
        categoryId = categoryId,
        title = title,
        description = description,
        schedules = schedules,
    )
}
