package com.li_routi.core.data.network.dto.request

data class CreateRoutineCategoryRequest(
    val name: String,
    val color: String? = null,
)

data class CreateRoutinesRequest(
    val routines: List<CreateRoutineRequestItem>,
)

data class CreateRoutineRequestItem(
    val categoryId: Long,
    val templateId: Long? = null,
    val name: String,
    val endTime: String? = null,
    val repeatDays: List<String>? = null,
    val alarmTime: String? = null,
)

/** PATCH /api/routines/{routineId} 요청 body. */
data class UpdateMemberRoutineRequest(
    val name: String,
    val endTime: String,
    val repeatDays: List<String>,
    val alarmTime: String? = null,
)

/** PATCH /api/routines/categories/{categoryId} 요청 body. */
data class UpdateRoutineCategoryRequest(
    val name: String,
    val color: String? = null,
)
