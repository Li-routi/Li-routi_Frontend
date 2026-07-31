package com.li_routi.core.data.network.dto.response

/** PUT /api/groups/{groupId}/routines/{routineId} 응답 result. */
data class GroupRoutineUpdateResultResponse(
    val routineId: Long,
    val groupId: Long,
    val categoryId: Long,
    val categoryName: String?,
    val title: String,
    val description: String,
    val schedules: List<GroupRoutineScheduleResponse>,
    val assignmentCount: Int,
)

data class GroupRoutineScheduleResponse(
    val repeatDay: String,
    val startTime: String,
    val endTime: String,
)
