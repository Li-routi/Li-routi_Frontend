package com.li_routi.core.domain.grouproutine

data class TodayGroupRoutine(
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
    val status: GroupRoutineStatus,
)
