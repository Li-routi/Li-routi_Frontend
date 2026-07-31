package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class CreateGroupRoutineUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        categoryId: Long,
        templateId: Long?,
        name: String,
        endTime: String?,
        repeatDays: List<RepeatDay>?,
        alarmTime: String?,
    ): ResultState<GroupRoutineCreateResult> = repository.createGroupRoutine(
        groupId = groupId,
        categoryId = categoryId,
        templateId = templateId,
        name = name,
        endTime = endTime,
        repeatDays = repeatDays,
        alarmTime = alarmTime,
    )
}
