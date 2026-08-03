package com.li_routi.core.data.network.dto.response

/** GET /api/groups/routines/today 응답 result. */
data class TodayGroupRoutineListResponse(
    val routines: List<TodayGroupRoutineResponse>,
)

data class TodayGroupRoutineResponse(
    val assignmentId: Long,
    val routineId: Long,
    val groupId: Long,
    val groupName: String,
    val categoryId: Long,
    val categoryName: String,
    val title: String,
    val description: String,
    val assignedDate: String,
    val scheduledStartTime: String,
    val scheduledEndTime: String,
    val status: String,
)
