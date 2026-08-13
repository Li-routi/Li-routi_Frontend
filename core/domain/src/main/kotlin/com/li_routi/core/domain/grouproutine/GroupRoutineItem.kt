package com.li_routi.core.domain.grouproutine

data class GroupRoutineItem(
    val routineId: Long,
    val categoryId: Long,
    val categoryName: String,
    val title: String,
    val description: String,
    val schedules: List<GroupRoutineSchedule>,
)
