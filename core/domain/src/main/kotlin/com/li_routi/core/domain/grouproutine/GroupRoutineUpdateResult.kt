package com.li_routi.core.domain.grouproutine

data class GroupRoutineUpdateResult(
    val routineId: Long,
    val groupId: Long,
    val categoryId: Long,
    val categoryName: String?,
    val title: String,
    val description: String,
    val schedules: List<GroupRoutineSchedule>,
    val assignmentCount: Int,
)
