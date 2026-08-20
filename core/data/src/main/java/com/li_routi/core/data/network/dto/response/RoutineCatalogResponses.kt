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
    /** HH:mm. 설정하지 않은 기존 루틴에서는 생략될 수 있다. */
    val startTime: String? = null,
    val endTime: String?,
    val repeatDays: List<String> = emptyList(),
    val alarmTime: String?,
    val completedToday: Boolean = false,
)
