package com.li_routi.core.data.network.dto.request

data class UpdateGroupRoutineRequest(
    val categoryId: Long,
    val title: String,
    val description: String,
    val schedules: List<GroupRoutineScheduleRequest>,
)

data class GroupRoutineScheduleRequest(
    val repeatDay: String,
    val startTime: String,
    val endTime: String,
)
