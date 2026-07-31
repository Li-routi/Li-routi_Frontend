package com.li_routi.core.domain.grouproutine

data class GroupRoutineCreateResult(
    val routines: List<CreatedGroupRoutine>,
    val activeRoutineCount: Long,
)
