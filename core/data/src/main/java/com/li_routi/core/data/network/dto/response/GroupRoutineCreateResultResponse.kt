package com.li_routi.core.data.network.dto.response

/** POST /api/groups/{groupId}/routines 응답 result. */
data class GroupRoutineCreateResultResponse(
    val routines: List<GroupRoutineResponse>,
    val activeRoutineCount: Long,
)

data class GroupRoutineResponse(
    val routineId: Long,
    val categoryId: Long,
    val categoryName: String,
    val templateId: Long?,
    val name: String,
    val endTime: String,
    val repeatDays: List<String>,
    val alarmTime: String?,
)
