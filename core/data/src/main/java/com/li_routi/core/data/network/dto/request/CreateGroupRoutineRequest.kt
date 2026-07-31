package com.li_routi.core.data.network.dto.request

data class CreateGroupRoutineRequest(
    val categoryId: Long,
    val templateId: Long?,
    val name: String,
    val endTime: String?,
    val repeatDays: List<String>?,
    val alarmTime: String?,
)
