package com.li_routi.core.domain.grouproutine

data class GroupRoutineSchedule(
    val repeatDay: RepeatDay,
    val startTime: String,
    val endTime: String,
)
