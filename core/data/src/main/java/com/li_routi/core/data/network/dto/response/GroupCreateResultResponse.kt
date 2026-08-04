package com.li_routi.core.data.network.dto.response

data class GroupCreateResultResponse(
    val groupId: Long,
    val name: String,
    val routines: List<GroupRoutineUpdateResultResponse>,
    val assignmentCount: Int,
)
