package com.li_routi.core.domain.grouproutine

data class CreatedGroup(
    val groupId: Long,
    val name: String,
    val routines: List<GroupRoutineUpdateResult>,
    val assignmentCount: Int,
)
