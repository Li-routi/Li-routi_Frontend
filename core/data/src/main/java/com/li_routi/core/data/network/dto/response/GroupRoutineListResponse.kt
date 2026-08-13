package com.li_routi.core.data.network.dto.response

data class GroupRoutineListResponse(
    val routines: List<GroupRoutineListItemResponse>,
)

data class GroupRoutineListItemResponse(
    val routineId: Long,
    val categoryId: Long,
    val categoryName: String,
    val title: String,
    val description: String,
    val schedules: List<GroupRoutineScheduleResponse>,
)
