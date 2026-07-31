package com.li_routi.core.domain.grouproutine

data class CreatedGroupRoutine(
    val routineId: Long,
    val categoryId: Long,
    val categoryName: String,
    val templateId: Long?,
    val name: String,
    val endTime: String,
    val repeatDays: List<RepeatDay>,
    val alarmTime: String?,
)
