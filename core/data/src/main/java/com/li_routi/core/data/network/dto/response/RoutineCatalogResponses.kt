package com.li_routi.core.data.network.dto.response

data class RoutineCategoryListResponse(
    val categories: List<RoutineCategoryResponse> = emptyList(),
    val addableCount: Int = 0,
)

data class RoutineCategoryResponse(
    val categoryId: Long,
    val name: String,
    val color: String?,
    val fixed: Boolean = false,
)

data class RoutineTemplateListResponse(
    val templates: List<RoutineTemplateResponse> = emptyList(),
)

data class RoutineTemplateResponse(
    val templateId: Long,
    val categoryId: Long,
    val categoryName: String,
    val name: String,
    val alreadyAdded: Boolean = false,
)

data class CreateRoutinesResultResponse(
    val routines: List<CreatedRoutineResponse> = emptyList(),
    val activeRoutineCount: Int = 0,
)

data class CreatedRoutineResponse(
    val routineId: Long,
    val categoryId: Long,
    val categoryName: String,
    val templateId: Long?,
    val name: String,
    val endTime: String?,
    val repeatDays: List<String> = emptyList(),
    val alarmTime: String?,
    val completedToday: Boolean = false,
)
